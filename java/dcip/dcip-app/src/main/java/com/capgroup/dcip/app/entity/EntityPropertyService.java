package com.capgroup.dcip.app.entity;

import com.capgroup.dcip.app.common.PropertyModel;

/**
 * Interface for operations on EntityPrpoerty
 */
public interface EntityPropertyService {
    Iterable<PropertyModel> findAllPropertiesForEntity(long entityId,
                                                             Class<?> entityType);

    Iterable<PropertyModel> findPropertiesForEntityAndPath(long entityId,
                                                                 Class<?> entityType,
                                                                 String pathPrefix);

    PropertyModel getPropertyForEntity(long entityId, Class<?> entityType, String key);

    PropertyModel createOrUpdateProperty(long entityId, PropertyModel propertyModel);
}
