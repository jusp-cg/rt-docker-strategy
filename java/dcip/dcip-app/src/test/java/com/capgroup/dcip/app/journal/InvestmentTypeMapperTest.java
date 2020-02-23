package com.capgroup.dcip.app.journal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import com.capgroup.dcip.domain.journal.InvestmentType;

@ContextConfiguration(classes = MappersTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(InvestmentTypeMapperImpl.class)
public class InvestmentTypeMapperTest {
	@Autowired
	InvestmentTypeMapper investmentTypeMapper;

	@Test
	public void toProfileModelTest() {
		EntityType entityType = new EntityType(5, "entityTypeName", "entityTypeDescription");
		User user = new User(13, "initials", "userName", new InvestmentUnit());
		Role role = new Role(14, "roleName", "roleDescription");
		InvestmentType investmentType = new InvestmentType(34, "it name", "it description", true);
		investmentType.setEntityType(entityType);
		Profile profile = new Profile(15, user, role);
		Event event = new Event(UUID.randomUUID(), "CREATE_PROFILE", profile, "me", 1l, UUID.randomUUID(), Collections.singleton(investmentType).stream());

		InvestmentTypeModel result = investmentTypeMapper.map(investmentType);

		assertEquals((long)investmentType.getId(), (long)result.getId());
		assertEquals(5, result.getEntityTypeId());
		assertEquals("it name", result.getName());
		assertEquals(15, result.getProfileId());
		assertEquals("it description", result.getDescription());
		assertTrue(result.isDefaultFlag());
	}

}
