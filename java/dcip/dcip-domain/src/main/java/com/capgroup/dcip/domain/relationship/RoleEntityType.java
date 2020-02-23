package com.capgroup.dcip.domain.relationship;

import com.capgroup.dcip.domain.entity.EntityType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
@NoArgsConstructor
public class RoleEntityType {
    @Id
    @Column
    private Long Id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private RoleType roleType;

    @ManyToOne
    @JoinColumn(name = "entity_type_id", nullable = false)
    private EntityType entityType;

    public RoleEntityType(RoleType roleType, EntityType entityType) {
        this(null, roleType, entityType);
    }

    public RoleEntityType(Long id, RoleType roleType, EntityType entityType) {
        Id = id;
        this.roleType = roleType;
        this.entityType = entityType;
    }
}
