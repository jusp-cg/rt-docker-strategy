package com.capgroup.dcip.app.event;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.domain.entity.EntityType;
import com.capgroup.dcip.domain.entity.EntityTypeService;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.event.Event;
import com.capgroup.dcip.infrastructure.repository.EntityTypeRepository;
import org.hibernate.BaseSessionEventListener;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Responsible for adding the boiler plate properties to an Entity (e.g.
 * event/version/valid period/etc.)
 */
@Component
public class EntityDecorator implements PreInsertEventListener, PreUpdateEventListener, PostDeleteEventListener {
    private static final long serialVersionUID = 3622489272941351576L;

    private EntityManager entityManager;
    private EntityTypeRepository entityTypeRepository;
    private EventSupportService eventSupportService;
    private EntityTypeService entityTypeService;

    @Autowired
    public EntityDecorator(EntityManagerFactory entityManagerFactory, EntityManager entityManager,
                           EntityTypeRepository entityTypeRepository, EventSupportService eventSupportService,
                           EntityTypeService entityTypeService) {
        this.entityManager = entityManager;
        this.entityTypeRepository = entityTypeRepository;
        this.eventSupportService = eventSupportService;
        this.entityTypeService = entityTypeService;

        registerForPersistentLifecycleEvents(entityManagerFactory);
    }

    /**
     * register for callbacks for entity persistent lifecycle changes
     */
    private void registerForPersistentLifecycleEvents(EntityManagerFactory entityManagerFactory) {
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) entityManagerFactory.unwrap(SessionFactory.class);
        EventListenerRegistry eventListeningRegistry = sessionFactory.getServiceRegistry()
                .getService(EventListenerRegistry.class);
        eventListeningRegistry.appendListeners(EventType.POST_DELETE, this);
        eventListeningRegistry.appendListeners(EventType.PRE_UPDATE, this);
        eventListeningRegistry.appendListeners(EventType.PRE_INSERT, this);
    }

    /**
     * Invoked before writing a new entity to the database
     */
    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        return onUpsert(event, event.getState());
    }

    /**
     * Invoked before updating an existing entity in the database
     */
    @Override
    public boolean onPreUpdate(PreUpdateEvent evnt) {
        return onUpsert(evnt, evnt.getState());
    }

    /**
     * Updates the boilerplate data of an entity i.e. event/validPeriod/id/version
     * etc.
     */
    boolean onUpsert(AbstractPreDatabaseOperationEvent evnt, Object[] state) {
        if (!(evnt.getEntity() instanceof TemporalEntity))
            return false;

        TemporalEntity entity = (TemporalEntity) evnt.getEntity();
        Event event = eventSupportService.getCurrentEvent();

        assert event != null;

        // if the latest event has already been applied to the entity - no need to
        // re-apply
        if (entity.getEvent() != null && entity.getEvent().equals(event)) {
            return false;
        }

        // to prevent the entity being written to the database multiple times, the data
        // on the entity has to be in sync with the data in the state. Updating one and
        // not updating the other causes unexpected behavior
        List<String> propertyNames = Arrays.asList(evnt.getPersister().getPropertyNames());
        entity.setEvent(event);
        state[propertyNames.indexOf("event")] = entity.getEvent();
        entity.setVersionId(UUID.randomUUID());
        state[propertyNames.indexOf("versionId")] = entity.getVersionId();
        entity.setValidPeriod(new LocalDateTimeRange(event.getCreatedTimestamp()));
        state[propertyNames.indexOf("validPeriod")] = entity.getValidPeriod();

        if (entity.getStatus() == null) {
            entity.setStatus(TemporalEntity.Status.ACTIVE);
            state[propertyNames.indexOf("status")] = entity.getStatus();
        }

        // only update the entity type if it has not been set -
        if (entity.getEntityType() == null) {
            EntityType entityType = entityTypeService.findEntityTypeForClass(entity.getClass())
                    .orElseThrow(() -> new ResourceNotFoundException("EntityType",
                        entity.getClass().getName()));
            entity.setEntityType(entityType);
            state[propertyNames.indexOf("entityType")] = entityType;
        }

        return false;
    }

    /**
     * Direct update to the database to set the event id of the deleted entity
     */
    @Override
    public void onPostDelete(PostDeleteEvent evnt) {
        if (!(evnt.getEntity() instanceof TemporalEntity))
            return;

        TemporalEntity entity = (TemporalEntity) evnt.getEntity();
        Event event = eventSupportService.getCurrentEvent();
        assert event != null;

        // update the entity - set the event id for the entity that is deleted
        Session session = entityManager.unwrap(Session.class);
        session.addEventListeners(new BaseSessionEventListener() {
            private static final long serialVersionUID = -5184380582165943328L;

            boolean flushing = true;

            @Override
            public void flushEnd(int numberOfEntities, int numberOfCollections) {
                // stops stack overflow/recursion
                if (flushing) {
                    flushing = false;
                    Query query = entityManager
                            .createNativeQuery("UPDATE entity SET event_id = ? WHERE status = 2 AND id = ?");
                    query.setParameter(1, event.getId());
                    query.setParameter(2, entity.getId());
                    query.executeUpdate();
                }
            }
        });
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}
