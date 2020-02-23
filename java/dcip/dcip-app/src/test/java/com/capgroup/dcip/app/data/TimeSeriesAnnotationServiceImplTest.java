package com.capgroup.dcip.app.data;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.ServiceTestConfig;
import com.capgroup.dcip.domain.annotation.Annotation;
import com.capgroup.dcip.infrastructure.repository.AnnotationRepository;


@ContextConfiguration(classes = ServiceTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(TimeSeriesAnnotationServiceImpl.class)
public class TimeSeriesAnnotationServiceImplTest {
	@Autowired
	TimeSeriesAnnotationService annotationService;
	
	@MockBean
	TimeSeriesAnnotationMapper mapper;
	
	@MockBean
	EntityManager entityManager;
	
	@MockBean
	AnnotationRepository repository;
	
	@Test
	public void findByIdTest() {
		Annotation annotation = new Annotation();
		TimeSeriesAnnotationModel model = new TimeSeriesAnnotationModel();
		
		// setup
		given(repository.findById(12L)).willReturn(Optional.of(annotation));
		given(mapper.map(annotation)).willReturn(model);
		
		TimeSeriesAnnotationModel result = annotationService.findById(12);
		
		assertSame(model , result);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void findByIdNotFoundTest() {
		given(repository.findById(12L)).willReturn(Optional.empty());
		
		annotationService.findById(12);
	}
	
	@Test
	public void findByTimeSeriesDefinitionidTest() {
		Stream<Annotation> annotations = Arrays.asList(new Annotation(), new Annotation()).stream();
		TimeSeriesAnnotationModel model = new TimeSeriesAnnotationModel();
		
		given(repository.findByEntityId(12)).willReturn(annotations);
		given(mapper.mapAnnotation(annotations)).willReturn(Arrays.asList(model).stream());
		
		List<TimeSeriesAnnotationModel> results = annotationService.findByTimeSeriesDefinitionId(12);
		
		assertSame(results.get(0), model);
	}
	
	@Test
	public void createTest() {
		Annotation annotation = new Annotation();
		List<Annotation> annotations = Collections.singletonList(annotation);
		TimeSeriesAnnotationModel model = new TimeSeriesAnnotationModel();
		List<TimeSeriesAnnotationModel> models = Collections.singletonList(model);
		
		given(mapper.mapModel(Mockito.eq(12L), Mockito.any())).willReturn(annotations.stream());
		
		given(repository.saveAll(Mockito.anyList())).willReturn(annotations);
		given(mapper.mapAnnotation(Mockito.any())).willReturn(models.stream());
		
		List<TimeSeriesAnnotationModel> result = annotationService.create(12, Collections.singletonList(model));
		
		assertSame(model, result.get(0));
		
		then(entityManager).should().flush();
	}
	
	@Test
	public void updateTest() {
		Annotation annotation = new Annotation();
		TimeSeriesAnnotationModel model = new TimeSeriesAnnotationModel();
		
		given(repository.findById(12L)).willReturn(Optional.of(annotation));
		given(repository.save(annotation)).willReturn(annotation);
		
		given(mapper.map(annotation)).willReturn(model);
		
		TimeSeriesAnnotationModel result = annotationService.update(12, 12, model);
		
		assertSame(model, result);
		
		then(entityManager).should().flush();
		then(mapper).should().update(model, annotation);
	}	
}
