package com.capgroup.dcip.app.entity;

import com.capgroup.dcip.domain.entity.EntityProperty;
import com.capgroup.dcip.domain.identity.User;
import com.capgroup.dcip.infrastructure.repository.EntityPropertyRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Retrieves EntityProperties for the User type
 */
@Component
@Order(2)
public class UserEntityPropertySelector implements EntityPropertySelector {

    EntityPropertyRepository repository;

    public UserEntityPropertySelector(EntityPropertyRepository entityPropertyRepository) {
        this.repository = entityPropertyRepository;
    }

    @Override
    public boolean canFind(Class<?> type, boolean applyHierarchyRules) {
        return applyHierarchyRules && type.equals(User.class);
    }

    @Override
    public Iterable<EntityProperty> findAllForEntity(long entityId, String path) {
        return repository.findAllForUser(entityId, path == null ? null : path + "/%", null);
    }

    @Override
    public Iterable<EntityProperty> findAllForEntity(long entityId) {
        return repository.findAllForUser(entityId, null, null);
    }

    @Override
    public Optional<EntityProperty> getProperty(long entityId, String key) {
        return StreamSupport.stream(repository.findAllForUser(entityId, null, key).spliterator(), false).findFirst();
    }
}
