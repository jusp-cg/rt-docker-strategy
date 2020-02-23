package com.capgroup.dcip.app.identity;

import com.capgroup.dcip.app.common.PropertyModel;


import java.util.List;

/**
 * Service for creating/updating/manipulating User entities and properties associated with a User
 */
public interface UserService {
    List<UserModel> findAll(String userInitials, Long applicationRoleId);

    UserModel findByInitials(String initials);

    Iterable<PropertyModel> findPropertiesFor(long userId);

    Iterable<PropertyModel> findPropertiesFor(long user, String path);

    UserModel findById(long userId);

    static UserModel findById(UserService userService, long userId) {
    	return userService.findById(userId);
    }

    PropertyModel getProperty(long userId, String propertyName);

    PropertyModel createOrUpdateProperty(long userId, PropertyModel model);

    Iterable<UserModel> findOnBehalfOfForUser(long userId);
}
