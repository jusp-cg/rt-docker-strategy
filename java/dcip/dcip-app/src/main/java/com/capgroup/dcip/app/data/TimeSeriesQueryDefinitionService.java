package com.capgroup.dcip.app.data;

import java.util.List;

/**
 * Contract for reading/writing and querying TimeSeriesQueryDefinitions
 */
public interface TimeSeriesQueryDefinitionService {

	TimeSeriesQueryDefinitionModel create(TimeSeriesQueryDefinitionModel definition);

	List<TimeSeriesQueryDefinitionModel> findAll(Long profileId);

	TimeSeriesQueryDefinitionModel findById(long timeSeriesDefinitionId);

	TimeSeriesQueryDefinitionModel delete(long id);

	TimeSeriesQueryDefinitionModel update(long id, TimeSeriesQueryDefinitionModel model);
}
