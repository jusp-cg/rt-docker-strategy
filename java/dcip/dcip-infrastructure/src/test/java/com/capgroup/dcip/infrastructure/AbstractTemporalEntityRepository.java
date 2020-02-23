package com.capgroup.dcip.infrastructure;

import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.domain.common.ZonedDateTimeRange;
import com.capgroup.dcip.domain.entity.EntityType;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.event.Event;
import com.capgroup.dcip.domain.event.EventParticipant;
import com.capgroup.dcip.domain.identity.InvestmentUnit;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.Role;
import com.capgroup.dcip.domain.identity.User;
import com.capgroup.dcip.infrastructure.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for testing Temporal Entities
 */
@Transactional
public class AbstractTemporalEntityRepository<TEntityType extends TemporalEntity> {

    protected Class<TEntityType> entityType;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EntityTypeRepository entityTypeRepository;

    protected AbstractTemporalEntityRepository(Class<TEntityType> entityType) {
        this.entityType = entityType;
    }

    protected void preSave(String eventType, Long entityTypeId, TemporalEntity... entities) throws Exception {
        preSave(eventType, entityTypeId, "XXX", entities);
    }

    protected void preSave(String eventType, Long entityTypeId, String userInitials, TemporalEntity... entities) throws Exception {
        // create the user
        User user = new User(userInitials, "Fred", new InvestmentUnit());
        user.setEntityType(entityTypeRepository.findById(1).get());

        // create the role
        Role role = new Role("PM", "In my dreams");
        role.setEntityType(entityTypeRepository.findById(3).get());

        // create the profile
        Profile profile = new Profile(user, role);
        profile.setEntityType(entityTypeRepository.findById(2).get());

        // set the entity type
        //long typeId = entityType.getField("TYPE_ID").getLong(null);
        EntityType entityType = entityTypeRepository.findById(entityTypeId).get();
        entityTypeRepository.save(entityType);
        Arrays.stream(entities).forEach(e -> {
            e.setEntityType(entityType);
        });

        List<EventParticipant> allEntities = new ArrayList<EventParticipant>(Arrays.asList(entities));
        allEntities.addAll(Arrays.asList(user, role, profile));

        // create the event
        Event event = new Event(UUID.randomUUID(), eventType, profile, "me", 1l, UUID.randomUUID(), allEntities.stream());
        Event event1 = eventRepository.save(event);

        allEntities.stream().map(e -> {
            return (TemporalEntity) e;
        }).forEach(e -> {
            e.setVersionId(UUID.randomUUID());
            e.setValidPeriod(new LocalDateTimeRange());
            e.setEvent(event1);
        });

        userRepository.save(user);
        roleRepository.save(role);
        profileRepository.save(profile);
        event.setProfile(profile);

    }
}
