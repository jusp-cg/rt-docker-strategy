package com.capgroup.dcip.app.relationship;

import com.capgroup.dcip.app.entity.TemporalEntityModel;
import com.capgroup.dcip.app.event.application.DeletedApplicationEvent;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.relationship.Relationship;
import com.capgroup.dcip.infrastructure.repository.RelationshipRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Reacts to events that affect relationships
 */
@Component
@Slf4j
public class RelationshipEventHandler {

    RelationshipRepository relationshipRepository;

    public RelationshipEventHandler(RelationshipRepository relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
    }

    @EventListener
    public void onEntityDeleted(DeletedApplicationEvent<?> deletedDomainEvent) {
        // only process temporal entities
        if (!(deletedDomainEvent.getTarget() instanceof TemporalEntityModel))
            return;

        TemporalEntityModel target = (TemporalEntityModel) deletedDomainEvent.getTarget();

        if (log.isDebugEnabled()){
            log.debug("Processing deleted event for relationships");
        }

        // find all the active relationships that have this entity
        Set<Relationship> relationships = relationshipRepository.findAllByEntityIdAndStatus(target.getId(),
                TemporalEntity.Status.ACTIVE);

        // producer logic
        // if there are any relationships then reject the delete and mark all relationships as marked for deletion
        Set<Relationship> producerRelationships =
                relationships.stream().filter(x -> x.getEntityId1().equals(target.getId()))
                        .collect(Collectors.toSet());
        if (!producerRelationships.isEmpty()) {
            if (log.isDebugEnabled()){
                log.debug("Found producer relationships:{} for entity:{}",
                        String.join(",", producerRelationships.stream().map(x->x.getId().toString()).collect(Collectors.toList())),
                        target.getId());
            }
            deletedDomainEvent.onReject(evnt -> producerRelationships.forEach(r -> r.setStatus(TemporalEntity.Status.MARKED_FOR_DELETE)));
            deletedDomainEvent.reject();
        }

        // consumer logic - delete the relationships
        // consumers
        Set<Relationship> consumerRelationships =
                relationships.stream().filter(x -> x.getEntityId2().equals(target.getId()))
                        .collect(Collectors.toSet());
        consumerRelationships.forEach(consumer -> relationshipRepository.delete(consumer));
    }
}