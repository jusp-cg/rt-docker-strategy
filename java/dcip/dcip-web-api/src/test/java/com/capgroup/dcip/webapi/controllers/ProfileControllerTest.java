package com.capgroup.dcip.webapi.controllers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.web.servlet.MockMvc;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.identity.ProfileModel;
import com.capgroup.dcip.app.identity.ProfileService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ControllersTestConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebMvcTest(controllers = ProfileController.class, secure = false)
@Import(ProfileController.class)
public class ProfileControllerTest {
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	ProfileService profileService;
	
	@Test
	public void findAllWithNoItemsTest() throws Exception {
		// setup expectations
		List<ProfileModel> profiles = new ArrayList<ProfileModel>();

		given(profileService.findAll(null, null, null)).willReturn(profiles);

		mockMvc.perform(get("/api/dcip/profiles").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void getProfileNotFoundTest() throws Exception {
		given(profileService.findById(10l)).willThrow(new ResourceNotFoundException("Profile", "10"));

		mockMvc.perform(get("/api/dcip/profiles/10").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getProfileTest() throws Exception {

		ProfileModel profileModel = new ProfileModel();

		given(profileService.findById(10l)).willReturn(profileModel);

		// TODO validate the json response
		mockMvc.perform(get("/api/dcip/profiles/10").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@Test
	public void getProfilesForUserTest() throws Exception{
		ProfileModel profileModel = new ProfileModel();
		List<ProfileModel> profileModels = Arrays.asList(profileModel);

		
		given(profileService.findAll("stmf", null, null)).willReturn(profileModels);

		// TODO validate the json response
		mockMvc.perform(get("/api/dcip/profiles?userInitials=stmf").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
}
