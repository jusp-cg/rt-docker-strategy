package com.capgroup.dcip.app.data;

import java.util.List;

/**
 * Service for retrieving/updating/creating annotations associated with a
 * TimeSeries
 */
public interface TimeSeriesAnnotationService {
	TimeSeriesAnnotationModel findById(long id);

	List<TimeSeriesAnnotationModel> findByTimeSeriesDefinitionId(long id);

	List<TimeSeriesAnnotationModel> create(long timeSeriesDefinitionId, List<TimeSeriesAnnotationModel> model);

	TimeSeriesAnnotationModel update(long timeSeriesDefinitionId, long annotationId, TimeSeriesAnnotationModel model);
}
