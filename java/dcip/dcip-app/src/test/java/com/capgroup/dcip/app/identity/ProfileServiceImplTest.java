package com.capgroup.dcip.app.identity;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import javax.persistence.EntityManager;

import com.capgroup.dcip.app.common.LinkMapper;
import com.capgroup.dcip.app.entity.EntityPropertyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.ServiceTestConfig;
import com.capgroup.dcip.domain.identity.InvestmentUnit;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.Role;
import com.capgroup.dcip.domain.identity.User;
import com.capgroup.dcip.infrastructure.repository.ProfileRepository;

@SpringBootTest()
@ContextConfiguration(classes = ServiceTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(ProfileServiceImpl.class)
public class ProfileServiceImplTest {
	@MockBean
	ProfileRepository profileRepository;

	@MockBean
	ProfileMapper profileMapper;

	@MockBean
	EntityManager entityManager;

	@Autowired
	ProfileService profileService;

	@MockBean
	LinkMapper linkMapper;

	@MockBean
	EntityPropertyService entityPropertyService;

	@Test(expected = ResourceNotFoundException.class)
	public void canvasNotFoundTest() {
		given(profileRepository.findById(10l)).willReturn(Optional.empty());

		profileService.findById(10l);
	}

	@Test
	public void readOneTest() {
		Profile profile = new Profile(10l, new User("xxx", "xxxx", new InvestmentUnit()), new Role("abc", "def"));
		ProfileModel model = new ProfileModel();

		given(profileRepository.findById(10l)).willReturn(Optional.of(profile));
		given(profileMapper.toProfileModel(profile)).willReturn(model);

		ProfileModel result = profileService.findById(10l);

		assertSame(model, result);
	}
}
