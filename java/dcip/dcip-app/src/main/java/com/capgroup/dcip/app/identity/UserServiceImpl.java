package com.capgroup.dcip.app.identity;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.common.PropertyModel;
import com.capgroup.dcip.app.entity.EntityPropertyService;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.User;
import com.capgroup.dcip.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of UserService for CRUD operations on User Entities
 */
@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    EntityPropertyService entityPropertyService;
    UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository repository,
                           EntityPropertyService entityPropertyService,
                           UserMapper userMapper) {
        this.userRepository = repository;
        this.entityPropertyService = entityPropertyService;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserModel> findAll(String userInitials, Long applicationRoleId) {
        return userMapper.mapAll(userRepository.findAllByInitialsAndApplicationRole(userInitials, applicationRoleId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserModel findByInitials(String initials) {
        User user = userRepository.findByInitials(initials).orElseThrow(() ->
                new ResourceNotFoundException("User", initials));
        return userMapper.map(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<PropertyModel> findPropertiesFor(long userId) {
        return entityPropertyService.findAllPropertiesForEntity(userId, User.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<PropertyModel> findPropertiesFor(long userId, String path) {
        return entityPropertyService.findPropertiesForEntityAndPath(userId, User.class, path);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyModel getProperty(long profileId, String propertyName) {
        return entityPropertyService.getPropertyForEntity(profileId, Profile.class, propertyName);
    }

    @Override
    @Transactional
    public PropertyModel createOrUpdateProperty(long userId, PropertyModel propertyModel) {
        return entityPropertyService.createOrUpdateProperty(userId, propertyModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<UserModel> findOnBehalfOfForUser(long userId) {
        return userMapper.mapAll(userRepository.findByIdUnchecked(userId).getOnBehalfOfUsers());
    }

    @Override
    @Transactional(readOnly = true)
    public UserModel findById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User", Long.toString(userId)));
        return userMapper.map(user);
    }


}
