package com.capgroup.dcip.app.data;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.domain.annotation.Annotation;
import com.capgroup.dcip.infrastructure.repository.AnnotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Retrieves Annotations using the Repository and converts them to a model using
 * the Mapper
 */
@Service
public class TimeSeriesAnnotationServiceImpl implements TimeSeriesAnnotationService {

	private AnnotationRepository annotationRepository;
	private TimeSeriesAnnotationMapper timeSeriesAnnotationMapper;
	private EntityManager entityManager;

	@Autowired
	public TimeSeriesAnnotationServiceImpl(AnnotationRepository annotationRepository, TimeSeriesAnnotationMapper mapper,
			EntityManager manager) {
		this.annotationRepository = annotationRepository;
		this.timeSeriesAnnotationMapper = mapper;
		this.entityManager = manager;
	}

	@Transactional(readOnly = true)
	@Override
	public TimeSeriesAnnotationModel findById(long id) {
		return timeSeriesAnnotationMapper.map(annotationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Annotation", Long.toString(id))));
	}

	@Transactional(readOnly = true)
	@Override
	public List<TimeSeriesAnnotationModel> findByTimeSeriesDefinitionId(long id) {
		return timeSeriesAnnotationMapper.mapAnnotation(annotationRepository.findByEntityId(id))
				.collect(Collectors.toList());
	}

	@Transactional
	public TimeSeriesAnnotationModel create(TimeSeriesAnnotationModel model) {
		Annotation annotation = timeSeriesAnnotationMapper.map(model);
		annotation = annotationRepository.save(annotation);
		
		// write the changes to the DB
		entityManager.flush();
		
		return timeSeriesAnnotationMapper.map(annotation);
	}

	@Override
	@Transactional
	public List<TimeSeriesAnnotationModel> create(long id, List<TimeSeriesAnnotationModel> model) {
		Stream<Annotation> annotations = timeSeriesAnnotationMapper.mapModel(id, model.stream());
		annotations = StreamSupport
				.stream(annotationRepository.saveAll(annotations.collect(Collectors.toList())).spliterator(), false);

		entityManager.flush();

		return timeSeriesAnnotationMapper.mapAnnotation(annotations).collect(Collectors.toList());
	}

	@Override
	public TimeSeriesAnnotationModel update(long timeSeriesDefinitionId, long annotationId,
			TimeSeriesAnnotationModel model) {
		Annotation annotation = annotationRepository.findById(annotationId)
				.orElseThrow(() -> new ResourceNotFoundException("Annotation", Long.toString(annotationId)));

		timeSeriesAnnotationMapper.update(model, annotation);
		annotation = annotationRepository.save(annotation);

		entityManager.flush();

		return timeSeriesAnnotationMapper.map(annotation);
	}

}
