package com.capgroup.dcip.app.identity;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.MappersTestConfig;
import com.capgroup.dcip.domain.entity.EntityType;
import com.capgroup.dcip.domain.event.Event;
import com.capgroup.dcip.domain.identity.InvestmentUnit;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.Role;
import com.capgroup.dcip.domain.identity.User;

@ContextConfiguration(classes = MappersTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(ProfileMapperImpl.class)
public class ProfileMapperTest {

	@Autowired
	ProfileMapper profileMapper;

	@Test
	public void toProfileModelTest() {
		EntityType profileEntityType = new EntityType(2, "entityTypeName", "entityTypeDescription");
		User user = new User(13, "initials", "userName", new InvestmentUnit());
		Role role = new Role(14, "roleName", "roleDescription");
		Profile profile = new Profile(15, user, role);
		profile.setEntityType(profileEntityType);
		Event event = new Event(UUID.randomUUID(), "CREATE_PROFILE", profile, "me",
				1l, UUID.randomUUID(), Collections.singleton(profile).stream());

		ProfileModel result = profileMapper.toProfileModel(profile);

		assertEquals((long) profile.getId(), (long) result.getId());
		assertEquals(2, result.getEntityTypeId());
		assertEquals("initials", result.getInitials());
		assertEquals(15, result.getProfileId());
	}
}
