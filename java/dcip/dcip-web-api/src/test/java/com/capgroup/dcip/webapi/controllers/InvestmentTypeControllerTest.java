package com.capgroup.dcip.webapi.controllers;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import com.capgroup.dcip.webapi.controllers.journal.InvestmentTypeController;
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

import com.capgroup.dcip.app.journal.InvestmentTypeModel;
import com.capgroup.dcip.app.journal.InvestmentTypeService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ControllersTestConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebMvcTest(controllers = InvestmentTypeController.class, secure = false)
@Import(InvestmentTypeController.class)
public class InvestmentTypeControllerTest {
	@MockBean
	InvestmentTypeService investmentTypeService;

	@Autowired
	MockMvc mockMvc;

	@Test
	public void findAllNoParameters() throws Exception {
		given(investmentTypeService.findByUserInitials(null, null)).willReturn(Collections.emptyList());

		mockMvc.perform(get("/api/dcip/journal/investment-types").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));
		;
	}

	@Test
	public void findAllWithParameters() throws Exception {
		given(investmentTypeService.findByUserInitials("25", true)).willReturn(Collections.emptyList());

		mockMvc.perform(
				get("/api/dcip/journal/investment-types?userInitials=25&includeDefaults=true").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));

		then(investmentTypeService).should().findByUserInitials("25", true);
	}

	@Test
	public void getWithResults() throws Exception {
		InvestmentTypeModel result = new InvestmentTypeModel();
		result.setDescription("description");
		result.setEntityTypeId(12l);
		result.setId(2434l);
		result.setName("ut name");
		result.setDefaultFlag(true);
		result.setProfileId(435435l);
		given(investmentTypeService.findByUserInitials(null, null)).willReturn(Arrays.asList(result));

		mockMvc.perform(
				get("/api/dcip/journal/investment-types").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].description").value("description"))
				.andExpect(jsonPath("$[0].entityTypeId").value("12"))
				.andExpect(jsonPath("$[0].id").value("2434"))
				.andExpect(jsonPath("$[0].name").value("ut name"))
				.andExpect(jsonPath("$[0].defaultFlag").value("true"));
	}
}
