package com.capgroup.dcip.app.entity;

import com.capgroup.dcip.domain.entity.EntityProperty;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.infrastructure.repository.EntityPropertyRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Retrieves EntityProperties that are associated with Profiles
 */
@Component
@Order(1)
public class ProfileEntityPropertySelector implements EntityPropertySelector {

    EntityPropertyRepository repository;

    public ProfileEntityPropertySelector(EntityPropertyRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean canFind(Class<?> type, boolean applyHierarchyRules) {
        return applyHierarchyRules && type.equals(Profile.class);
    }

    @Override
    public Iterable<EntityProperty> findAllForEntity(long entityId, String path) {
        return repository.findAllForProfile(entityId, path == null ? null : path + "/%", null);
    }

    @Override
    public Iterable<EntityProperty> findAllForEntity(long entityId) {
        return repository.findAllForProfile(entityId, null, null);
    }

    @Override
    public Optional<EntityProperty> getProperty(long entityId, String key) {
        return StreamSupport.stream(repository.findAllForProfile(entityId, null, key).spliterator(), false)
                .findFirst();
    }
}
