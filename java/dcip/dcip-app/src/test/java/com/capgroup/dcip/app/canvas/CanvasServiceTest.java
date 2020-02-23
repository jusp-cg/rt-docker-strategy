package com.capgroup.dcip.app.canvas;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.domain.identity.Profile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.ServiceTestConfig;
import com.capgroup.dcip.domain.canvas.Canvas;
import com.capgroup.dcip.infrastructure.repository.CanvasRepository;
import com.capgroup.dcip.infrastructure.repository.WorkbenchResourceRepository;

@SpringBootTest()
@ContextConfiguration(classes = ServiceTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(CanvasServiceImpl.class)
public class CanvasServiceTest {
	@MockBean
	CanvasRepository canvasRepository;

	@MockBean
	CanvasMapper canvasMapper;

	@MockBean
	EntityManager entityManager;

	@Autowired
	CanvasService canvasService;
	
	@MockBean
	WorkbenchResourceRepository workbenchResourceRepository;
	
	@MockBean
	WorkbenchResourceMapper workbenchResourceMapper;

	@MockBean
	RequestContextService requestContextService;

	@Test
	public void createTest() {
		Canvas canvas = new Canvas();
		CanvasModel result = new CanvasModel();

		given(requestContextService.currentProfile()).willReturn(new Profile(1, null, null));
		given(canvasMapper.map(any(CanvasModel.class))).willReturn(canvas);
		given(canvasRepository.save(canvas)).willReturn(canvas);
		given(canvasMapper.map(canvas)).willReturn(result);

		CanvasModel model = canvasService.createCanvas(result);

		assertSame(result, model);
	}

	public void updateTest() {
		Canvas canvas = new Canvas("oldName", "oldDescription");
		CanvasModel result = new CanvasModel();


		given(canvasRepository.findById(10L)).willReturn(Optional.of(canvas));
		given(canvasRepository.save(canvas)).willReturn(canvas);
		given(canvasMapper.map(canvas)).willReturn(result);

		CanvasModel model = canvasService.updateCanvas(10, result);

		assertSame(result, model);
		then(canvasMapper).should().updateCanvas(any(CanvasModel.class), eq(canvas));
	}

	@Test
	public void readAllWithNoItemsTest() {
		// setup expectations
		List<Canvas> canvases = new ArrayList<Canvas>();

		given(canvasRepository.findAll()).willReturn(canvases);
		given(canvasMapper.mapToCanvasModels(ArgumentMatchers.any())).willReturn(new ArrayList<>());

		Iterable<CanvasModel> results = canvasService.findAll("abc",1L);

		assertFalse(results.iterator().hasNext());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void canvasNotFoundTest() {
		given(canvasRepository.findById(10L)).willReturn(Optional.empty());

		canvasService.findCanvasById(10L);
	}

	@Test
	public void readOneTest() {
		Canvas canvas = new Canvas();
		CanvasModel model = new CanvasModel();

		given(canvasRepository.findById(10L)).willReturn(Optional.of(canvas));
		given(canvasMapper.map(canvas)).willReturn(model);

		CanvasModel result = canvasService.findCanvasById(10L);

		assertSame(model, result);
	}

	public void deleteTest() {
		Canvas canvas = new Canvas();
		CanvasModel result = new CanvasModel();

		given(canvasRepository.findById(10L)).willReturn(Optional.of(canvas));
		given(canvasMapper.map(canvas)).willReturn(result);

		canvasService.deleteCanvas(10L);

		then(canvasRepository).should().delete(canvas);
	}
}
