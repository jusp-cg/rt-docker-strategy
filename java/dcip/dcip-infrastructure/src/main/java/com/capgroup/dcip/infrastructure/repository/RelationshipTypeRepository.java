package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.relationship.RelationshipType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RelationshipTypeRepository extends CrudRepository<RelationshipType, Long> {

    @Query("select r from RelationshipType r where r.roleEntityType1.id = :roleEntityType1 and r.roleEntityType2.id = :roleEntityType2")
    RelationshipType findRelationshipTypeByRoleType(@Param("roleEntityType1") Long roleEntityType1, @Param("roleEntityType2") Long roleEntityType2);
}
