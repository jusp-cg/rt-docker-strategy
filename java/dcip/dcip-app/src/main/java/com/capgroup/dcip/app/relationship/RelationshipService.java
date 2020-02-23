package com.capgroup.dcip.app.relationship;

import com.capgroup.dcip.domain.relationship.RelationshipType;
import org.springframework.stereotype.Service;

@Service
public interface RelationshipService {

    RelationshipModel createRelation(RelationshipModel relationshipModel);

    void removeRelation(RelationshipModel relationshipModel);

    void removeRelationByProducerId(Long producerId);

    Iterable<RelationshipModel> findRelationsByConsumerId(Long id);

    RelationshipType findRelationshipTypeByRoleType(Long roleEntityType1, Long roleEntityType2);

    enum RELATION_TYPE {
        CONSUMER("Consumer"),
        PRODUCER("Producer");

        String name;

        RELATION_TYPE(String name) {
            this.name = name;
        }

        public String forName() {
            return name;
        }
    }
}