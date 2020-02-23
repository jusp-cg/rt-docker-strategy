package com.capgroup.dcip.app.identity;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.common.LinkMapper;
import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.common.PropertyModel;
import com.capgroup.dcip.app.entity.EntityPropertyService;
import com.capgroup.dcip.domain.identity.ApplicationRole;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.infrastructure.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProfileServiceImpl implements ProfileService {

    ProfileRepository profileRepository;
    ProfileMapper profileMapper;
    EntityPropertyService entityPropertyService;
    LinkMapper linkMapper;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository, ProfileMapper profileMapper,
                              EntityPropertyService entityPropertyService,
                              LinkMapper linkMapper) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
        this.entityPropertyService = entityPropertyService;
        this.linkMapper = linkMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ProfileModel> findAll(String userInitials, String role, Long investmentUnitId) {
        ProfileRepository.ExpressionBuilder builder = new ProfileRepository.ExpressionBuilder();
        Iterable<Profile> profiles = profileRepository.findAll(builder.hasUserInitials(userInitials)
                .and(builder.hasRole(role)).and(builder.hasInvestmentUnit(investmentUnitId)));
        return profileMapper.toProfileModels(profiles);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileModel findById(long id) {
        Profile result = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", Long.toString(id)));

        return profileMapper.toProfileModel(result);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<PropertyModel> findPropertiesFor(long profileId) {
        return entityPropertyService.findAllPropertiesForEntity(profileId, Profile.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<PropertyModel> findPropertiesFor(long profileId, String path) {
        return entityPropertyService.findPropertiesForEntityAndPath(profileId, Profile.class, path);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyModel getProperty(long profileId, String propertyName) {
        return entityPropertyService.getPropertyForEntity(profileId, Profile.class, propertyName);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<LinkModel<Long>> findAllProfileLinksByUserId(long userId) {
        return linkMapper.mapAll(profileRepository.findByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<LinkModel<Long>>> findAllProfileLinksByUserIds(List<Long> userIds) {
        return StreamSupport.stream(profileRepository.findAllByUserIdIn(userIds).spliterator(), false)
                .collect(Collectors.groupingBy(x -> x.getUser().getId(),
                        Collectors.mapping(linkMapper::mapEntity, Collectors.toList())));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("UserProfileEntitlement")
    public boolean isProfileValidForUser(long profileId, String user) {
        return profileRepository.isProfileValidForUser(profileId, user);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationRole.ApplicationRoleId applicationRoleForProfile(long profileId) {
        return ApplicationRole.ApplicationRoleId.valueOf(profileRepository.findByIdUnchecked(profileId).getApplicationRoleId());
    }
}
