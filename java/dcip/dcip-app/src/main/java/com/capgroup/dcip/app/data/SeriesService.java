package com.capgroup.dcip.app.data;

import com.capgroup.dcip.domain.data.SeriesFilter;

import java.util.EnumSet;
import java.util.Optional;

/**
 * Retrieve all the Series
 */
public interface SeriesService {
    Optional<SeriesModel> findById(long seriesId);

    SeriesModel findByIdOrElseThrow(long seriesId);

    Iterable<SeriesModel> findAll(EnumSet<SeriesFilter> filters);
}
