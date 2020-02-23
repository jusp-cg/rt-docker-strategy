package com.capgroup.dcip.app.entity;

import com.capgroup.dcip.domain.entity.EntityProperty;

import java.util.Optional;

/**
 * Accessor for retrieving entity properties.
 */
public interface EntityPropertySelector {
    boolean canFind(Class<?> type, boolean applyHierarchyRules);

    Iterable<EntityProperty> findAllForEntity(long entityId, String path);

    Iterable<EntityProperty> findAllForEntity(long entityId);

    Optional<EntityProperty> getProperty(long entityId, String key);
}
