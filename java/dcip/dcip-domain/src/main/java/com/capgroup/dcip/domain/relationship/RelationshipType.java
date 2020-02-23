package com.capgroup.dcip.domain.relationship;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
public class RelationshipType {

    @Id
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_entity_type1", nullable = false)
    RoleEntityType roleEntityType1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_entity_type2", nullable = false)
    RoleEntityType roleEntityType2;

    @Column
    @NotNull
    int direction;
}
