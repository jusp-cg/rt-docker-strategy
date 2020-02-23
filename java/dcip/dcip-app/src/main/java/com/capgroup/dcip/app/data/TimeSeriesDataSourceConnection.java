package com.capgroup.dcip.app.data;

import com.capgroup.dcip.domain.data.AuditableTimeSeries;
import com.capgroup.dcip.domain.data.TimeSeries;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Defines the contract for querying time series (concrete implementations of
 * web/database/etc.)
 */
public interface TimeSeriesDataSourceConnection {
    /**
     * Determines if this data source connection can handle the query
     */
    TimeSeriesDataSourceConnectionMatchResult matches(TimeSeriesCriteria query);

    /**
     * Retrieves a time series for the specified query
     */
    Map<TimeSeriesQuery, CompletableFuture<TimeSeries>> query(Collection<TimeSeriesQuery> query,
                                                              Executor executor);

    /**
     * Returns any auditAll for a Company/Series since the modifiedSince parameter
     */
    Map<TimeSeriesQuery, CompletableFuture<AuditableTimeSeries>> audits(Collection<TimeSeriesQuery> query,
                                                                        Executor executor);
}
