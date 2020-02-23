package com.capgroup.dcip.app.relationship;

import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.relationship.Relationship;
import com.capgroup.dcip.domain.relationship.RelationshipType;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * responsible for mapping between a Relationship (domain object) and CanvasRelationshipModel
 * (DTO), and vice-versa
 */
@Mapper(config = TemporalEntityMapper.class)
public abstract class CanvasRelationMapper {

    public CanvasRelationModel map(List<Relationship> relationships) {
        Map<Long, List<Long>> relation = new HashMap<>();
        relationships.forEach(r -> {
            if (relation.containsKey(r.getEntityId1())) {
                relation.get(r.getEntityId1()).add(r.getEntityId2());
            } else {
                List<Long> linkedEntities = new ArrayList<>();
                linkedEntities.add(r.getEntityId2());
                relation.put(r.getEntityId1(), linkedEntities);
            }
        });

        return new CanvasRelationModel(relation);
    }

    public List<Relationship> map(CanvasRelationModel relationModel) {
        List<Relationship> relationships = new ArrayList<>();
        Map<Long, List<Long>> relationMap = relationModel.getRelations();
        relationMap.keySet().forEach(key -> relationMap.get(key).forEach(val -> {
            Relationship relationship = new Relationship(key, val, new RelationshipType());
            relationships.add(relationship);
        }));

        return relationships;
    }
}
