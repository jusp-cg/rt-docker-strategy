package com.capgroup.dcip.webapi.controllers;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.capgroup.dcip.domain.common.LocalDateTimeRange;
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
import com.capgroup.dcip.app.canvas.CanvasModel;
import com.capgroup.dcip.app.canvas.CanvasService;
import com.capgroup.dcip.app.canvas.WorkbenchResourceService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ControllersTestConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebMvcTest(controllers = CanvasController.class, secure = false)
@Import(CanvasController.class)
public class CanvasControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	CanvasService canvasService;
	@MockBean
	WorkbenchResourceService workbenchResourceService;
	
	@Test
	public void findAllWithNoItemsTest() throws Exception {
		// setup expectations
		List<CanvasModel> canvases = new ArrayList<CanvasModel>();

		given(canvasService.findAll(null, null)).willReturn(canvases);

		mockMvc.perform(get("/api/dcip/canvases").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void getCanvasNotFoundTest() throws Exception {
		given(canvasService.findCanvasById(10l)).willThrow(new ResourceNotFoundException("Canvas", "10"));

		mockMvc.perform(get("/api/dcip/canvases/10").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getCanvas() throws Exception {

		CanvasModel canvasModel = new CanvasModel();

		given(canvasService.findCanvasById(10l)).willReturn(canvasModel);

		// TODO validate the json response
		mockMvc.perform(get("/api/dcip/canvases/10").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void postTest() throws Throwable {
		CanvasModel result = new CanvasModel("name", "description", null, null);

		given(canvasService.createCanvas(result)).willReturn(result);

		// TODO validate the json response
		mockMvc.perform(post("/api/dcip/canvases").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(result))).andExpect(status().isOk());
	}

	@Test
	public void putTest() throws Throwable {
		CanvasModel result = new CanvasModel("canvasName", "canvasDescription", null, null);
		result.setId(10l);
		result.setValidPeriod(
				new LocalDateTimeRange(LocalDateTime.of(2018, 1, 1, 12, 0, 0), LocalDateTime.of(2020, 3, 6, 2, 1, 1)));

		given(canvasService.updateCanvas(10l, result)).willReturn(result);

		mockMvc.perform(put("/api/dcip/canvases/10").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(result)))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("name").value("canvasName"))
				.andExpect(jsonPath("description").value("canvasDescription")).andExpect(jsonPath("id").value("10"))
				.andExpect(jsonPath("validPeriod.startDate").value("2018-01-01T12:00:00.000Z"))
				.andExpect(jsonPath("validPeriod.endDate").value("2020-03-06T02:01:01.000Z"));
	}

	@Test
	public void deleteTest() throws Throwable {

		mockMvc.perform(delete("/api/dcip/canvases/10")).andExpect(status().isOk());

		then(canvasService).should().deleteCanvas(10l);
	}

	@Test
	public void getForProfileNoItemsTest() throws Throwable {
		given(canvasService.findAll(null, 10l)).willReturn(Collections.emptyList());

		mockMvc.perform(get("/api/dcip/canvases?profileId=10").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
}
