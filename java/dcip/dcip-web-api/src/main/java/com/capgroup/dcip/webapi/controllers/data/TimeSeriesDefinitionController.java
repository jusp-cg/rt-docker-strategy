package com.capgroup.dcip.webapi.controllers.data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.capgroup.dcip.app.data.*;
import com.capgroup.dcip.domain.data.TimeSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * External REST API for retrieving/updating and deleting saved queries and
 * their annotations
 */
@RestController()
@RequestMapping("api/dcip/data/timeseries-definitions")
public class TimeSeriesDefinitionController {

	private TimeSeriesQueryDefinitionService timeSeriesQueryDefinitionService;
	private TimeSeriesService timeSeriesQueryService;
	private TimeSeriesAnnotationService timeSeriesAnnotationService;

	@Autowired
	public TimeSeriesDefinitionController(TimeSeriesQueryDefinitionService definitionSvc,
										  TimeSeriesService timeSeriesService, TimeSeriesAnnotationService annotationService) {
		this.timeSeriesQueryDefinitionService = definitionSvc;
		this.timeSeriesQueryService = timeSeriesService;
		this.timeSeriesAnnotationService = annotationService;
	}

	@PostMapping
	public TimeSeriesQueryDefinitionModel post(@RequestBody TimeSeriesQueryDefinitionModel model) {
		return timeSeriesQueryDefinitionService.create(model);
	}

	@GetMapping
	public List<TimeSeriesQueryDefinitionModel> get(@RequestParam(name = "profileId") Long profileId) {
		return timeSeriesQueryDefinitionService.findAll(profileId);
	}

	@GetMapping(value = "/{id}")
	public TimeSeriesQueryDefinitionModel get(@PathVariable("id") long timeSeriesId) {
		return timeSeriesQueryDefinitionService.findById(timeSeriesId);
	}
	
	@PutMapping(value = "/{id}")
	public TimeSeriesQueryDefinitionModel put(@PathVariable("id") long timeSeriesId,
			@RequestBody TimeSeriesQueryDefinitionModel body) {
		return timeSeriesQueryDefinitionService.update(timeSeriesId,  body);
	}


	@GetMapping(value = "/{id}/annotations")
	public Stream<TimeSeriesAnnotationModel> getAnnotations(@PathVariable("id") long timeSeriesId) {
		return timeSeriesAnnotationService.findByTimeSeriesDefinitionId(timeSeriesId).stream();
	}

	@GetMapping(value = "/{id}/annotations/{annotationId}")
	public TimeSeriesAnnotationModel getAnnotation(@PathVariable("id") long timeSeriesId,
			@PathVariable("annotationId") long annotationId) {
		return timeSeriesAnnotationService.findById(annotationId);
	}

	@PostMapping(value = "/{id}/annotations")
	public Stream<TimeSeriesAnnotationModel> createAnnotations(@PathVariable("id") long timeSeriesDefinitionId,
			@RequestBody List<TimeSeriesAnnotationModel> annotations) {
		return timeSeriesAnnotationService.create(timeSeriesDefinitionId, annotations).stream();
	}

	@PutMapping(value = "/{id}/annotations/{annotationId}")
	public TimeSeriesAnnotationModel updateAnnotation(@PathVariable("id") long timeSeriesDefinitionId,
			@PathVariable("annotationId") long annotationId, @RequestBody TimeSeriesAnnotationModel annotation) {
		return timeSeriesAnnotationService.update(timeSeriesDefinitionId, annotationId, annotation);
	}

	@GetMapping(value = "/{id}/timeseries")
	public TimeSeriesQueryResultModel<TimeSeries.Entry> getTimeSeries(@PathVariable("id") long timeSeriesId) {
		TimeSeriesQueryDefinitionModel model = timeSeriesQueryDefinitionService.findById(timeSeriesId);
		return new TimeSeriesQueryResultModel<>(model.toTimeSeriesQueryModel(), timeSeriesQueryService.query(model.toTimeSeriesQueryModel()));
	}

	@DeleteMapping(value = "/{id}")
	public TimeSeriesQueryDefinitionModel delete(@PathVariable("id") long timeSeriesId) {
		return timeSeriesQueryDefinitionService.delete(timeSeriesId);
	}
}
