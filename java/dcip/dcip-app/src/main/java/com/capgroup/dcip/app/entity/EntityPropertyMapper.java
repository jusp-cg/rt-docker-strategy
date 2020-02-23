package com.capgroup.dcip.app.entity;

import com.capgroup.dcip.app.common.PropertyModel;
import com.capgroup.dcip.domain.common.PropertyType;
import com.capgroup.dcip.domain.entity.EntityProperty;
import com.capgroup.dcip.domain.entity.EntityTypeService;
import com.capgroup.dcip.infrastructure.repository.common.PropertyTypeRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapping between a Entityroperty and EntityPropertyModel
 */
@Mapper(config = TemporalEntityMapper.class)
public abstract class EntityPropertyMapper {
    @Autowired
    private PropertyTypeRepository propertyTypeRepository;

    /**
     * Adds the additional property type and the user association
     */
    @AfterMapping
    void afterMapping(PropertyModel model, @MappingTarget EntityProperty result, @Context long entityId) {
        propertyTypeRepository.findByKeyIgnoreCase(model.getKey()).ifPresent(propertyType ->
                result.setPropertyType(propertyType)
        );
        result.setEntityId(entityId);
    }

    abstract EntityProperty map(PropertyModel model, @Context long entityId);

    abstract PropertyModel map(EntityProperty property);

    abstract Iterable<PropertyModel> mapAll(Iterable<EntityProperty> properties);

    @InheritConfiguration(name = "map")
    abstract void update(PropertyModel model, @MappingTarget EntityProperty property);

}
