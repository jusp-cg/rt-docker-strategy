package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.entity.EntityFinder;
import com.capgroup.dcip.domain.entity.EntityType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * JPA repository for CRUD operations on an EntityType
 */
public interface EntityTypeRepository extends CrudRepository<EntityType, Long>, EntityFinder<EntityType> {
    /**
     * note: This is a projection so the Entity returned should not be associated with other entities
     */
    @Query(value = "select distinct et.id, et.name, et.description, et.entity_url_template from entity e join entity_type et on e" +
            ".entity_type_id = et.id where e.id = :entityId", nativeQuery = true)
         Optional<EntityType> findEntityTypeByEntityId(@Param("entityId") long entityId);
}
