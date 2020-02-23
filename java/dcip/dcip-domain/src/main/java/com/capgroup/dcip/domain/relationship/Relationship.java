package com.capgroup.dcip.domain.relationship;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

@Entity
@Table(name = "relationship_view")
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call relationship_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call relationship_update(?, ?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call relationship_delete(?, ?)}")
@Getter
@Setter
@NoArgsConstructor
@Filter(name = "disaggregationVirtualView",
        condition = "(EXISTS(SELECT 1 FROM disaggregation_entity_view dev " +
                "JOIN entity_view ev ON dev.entity_id = ev.id and dev" +
                ".investment_unit_id = :investmentUnitId and dev.version_id = ev.version_id WHERE ev.id = entity1) " +
                "AND " +
                "EXISTS(SELECT 1 FROM disaggregation_entity_view dev " +
                "JOIN entity_view ev ON dev.entity_id = ev.id and dev" +
                ".investment_unit_id = :investmentUnitId and dev.version_id = ev.version_id WHERE ev.id = entity2))")
public class Relationship extends TemporalEntity {

    private static final long serialVersionUID = -3001725387608422105L;

    @Column(name = "entity1")
    private Long entityId1;

    @Column(name = "entity2")
    private Long entityId2;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_type")
    @Getter
    private RelationshipType relationshipType;

    public Relationship(Long firstEntityId, Long secondEntityId, RelationshipType relationshipType) {
        this.entityId1 = firstEntityId;
        this.entityId2 = secondEntityId;
        this.relationshipType = relationshipType;
    }
}