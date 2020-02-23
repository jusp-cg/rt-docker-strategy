package com.capgroup.dcip.app.entity;

import com.capgroup.dcip.domain.entity.EntityProperty;
import com.capgroup.dcip.infrastructure.repository.EntityPropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Retrieves EntityProperties without applying any hierarchy rules
 */
@Component
@Order
public class DefaultEntityPropertySelector implements EntityPropertySelector {

    EntityPropertyRepository entityPropertyRepository;

    @Autowired
    public DefaultEntityPropertySelector(EntityPropertyRepository repository) {
        this.entityPropertyRepository = repository;
    }

    @Override
    public boolean canFind(Class<?> type, boolean applyHierarchyRules) {
        return true;
    }

    @Override
    public Iterable<EntityProperty> findAllForEntity(long entityId, String path) {
        return entityPropertyRepository.findAllForUser(entityId, path == null ? null : path + "/%", null);
    }

    @Override
    public Iterable<EntityProperty> findAllForEntity(long entityId) {
        return entityPropertyRepository.findAllForEntity(entityId, null, null);
    }

    @Override
    public Optional<EntityProperty> getProperty(long entityId, String key) {
        return StreamSupport.stream(entityPropertyRepository.findAllForEntity(entityId, null, key).spliterator(),
                false).findAny();
    }
}
