package com.capgroup.dcip.app.entity;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.common.PropertyModel;
import com.capgroup.dcip.domain.entity.EntityProperty;
import com.capgroup.dcip.infrastructure.repository.EntityPropertyRepository;
import com.capgroup.dcip.infrastructure.repository.common.PropertyTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * implementation that reads/writes/updates EntityProperties - mapping tham to/from a PropertyModel
 */
@Service
public class EntityPropertyServiceImpl implements EntityPropertyService {

    List<EntityPropertySelector> finders;
    EntityPropertyMapper entityPropertyMapper;
    EntityPropertyRepository entityPropertyRepository;
    EntityManager entityManager;
    PropertyTypeRepository propertyTypeRepository;

    @Autowired
    public EntityPropertyServiceImpl(List<EntityPropertySelector> finders,
                                     EntityPropertyMapper mapper,
                                     EntityPropertyRepository entityPropertyRepository,
                                     EntityManager entityManager,
                                     PropertyTypeRepository propertyTypeRepository) {
        this.finders = finders;
        this.entityPropertyMapper = mapper;
        this.entityPropertyRepository = entityPropertyRepository;
        this.entityManager = entityManager;
        this.propertyTypeRepository = propertyTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<PropertyModel> findAllPropertiesForEntity(long entityId, Class<?> entityType) {
        return
                entityPropertyMapper.mapAll(
                        finders.stream().filter(x -> x.canFind(entityType, true)).findFirst().map(x -> x.findAllForEntity(entityId))
                                .orElseGet(Collections::emptyList));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<PropertyModel> findPropertiesForEntityAndPath(long entityId, Class<?> entityType,
                                                                  String pathPrefix) {
        // find the entity properties
        List<EntityProperty> entityProperties = new ArrayList<>();
        finders.stream().filter(x -> x.canFind(entityType, true)).findFirst().map(x -> x.findAllForEntity(entityId,
                pathPrefix))
                .orElseGet(Collections::emptyList).iterator().forEachRemaining(entityProperties::add);

        // find the defaults
        Stream<EntityProperty> propertyTypeProperties =
                StreamSupport.stream(entityPropertyRepository.findAllDefaults(pathPrefix + "/%", null).spliterator(), false).filter(y -> !entityProperties.stream().anyMatch(x -> x.getKey().equalsIgnoreCase(y.getKey())));

        return Stream.concat(entityProperties.stream().map(entityPropertyMapper::map),
                propertyTypeProperties.map(entityPropertyMapper::map)).collect(Collectors.toList());
    }

    @Override
    public PropertyModel getPropertyForEntity(long entityId, Class<?> entityType, String key) {
        // try and get the property from the EntityProperty table
        Optional<PropertyModel> result = finders.stream().filter(x -> x.canFind(entityType, true)).findFirst()
                .map(x -> x.getProperty(entityId, key).map(entityPropertyMapper::map)).orElseThrow(() -> new ResourceNotFoundException("EntityTypeId", key));
        // if the property is not found the get it the default
        return result.orElseGet(() -> StreamSupport.stream(entityPropertyRepository.findAllDefaults(null, key)
                .spliterator(), false).findAny()
                .map(entityPropertyMapper::map).orElseThrow(() -> new ResourceNotFoundException("EntityProperty", key)));
    }

    @Override
    @Transactional
    public PropertyModel createOrUpdateProperty(long entityId, PropertyModel model) {
        Optional<EntityProperty> existingProperty =
                StreamSupport.stream(entityPropertyRepository.findAllForEntity(entityId,
                        null, model.getKey()).spliterator(), false).findAny();

        // create of update existing property
        EntityProperty result = existingProperty.map(property -> {
                    entityPropertyMapper.update(model, property);
                    return property;
                }
        ).orElseGet(() -> {
            EntityProperty p = entityPropertyMapper.map(model, entityId);
            p = entityPropertyRepository.save(p);
            entityManager.flush();
            return p;
        });

        return entityPropertyMapper.map(result);
    }
}
