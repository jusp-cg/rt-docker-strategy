package com.capgroup.dcip.domain.entity;

import java.net.URL;
import java.util.Optional;

/**
 * Service for accessing entity type information
 */
public interface EntityTypeService {
    Optional<EntityType> findEntityTypeForClass(Class<?> clazz);

    Optional<Class<?>> findClassForEntityType(EntityType entityType);

    /**
     * From an EntityType and the id of an Entity, create the URL that references the entity
     */
    <T> Optional<URL> findResourceUrl(EntityType entityType, T entityId);
}
