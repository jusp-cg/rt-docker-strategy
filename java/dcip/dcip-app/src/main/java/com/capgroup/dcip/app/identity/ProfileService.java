package com.capgroup.dcip.app.identity;

import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.common.PropertyModel;
import com.capgroup.dcip.domain.identity.ApplicationRole;

import java.util.List;
import java.util.Map;

/**
 * Service for finding Profiles
 */
public interface ProfileService {
	Iterable<ProfileModel> findAll(String userInitials, String role, Long investmentUnitId);

	ProfileModel findById(long id);

	Iterable<PropertyModel> findPropertiesFor(long profileId);

	Iterable<PropertyModel> findPropertiesFor(long profileId, String path);

	PropertyModel getProperty(long profileId, String propertyName);

	Iterable<LinkModel<Long>> findAllProfileLinksByUserId(long userId);

	Map<Long, List<LinkModel<Long>>> findAllProfileLinksByUserIds(List<Long> userIds);

	boolean isProfileValidForUser(long profileId, String user);

	ApplicationRole.ApplicationRoleId applicationRoleForProfile(long profileId);
}
