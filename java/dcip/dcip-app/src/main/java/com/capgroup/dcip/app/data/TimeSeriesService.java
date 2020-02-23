package com.capgroup.dcip.app.data;

import com.capgroup.dcip.domain.data.AuditableTimeSeries;
import com.capgroup.dcip.domain.data.TimeSeries;

import java.util.Collection;
import java.util.List;

/**
 * Application level interface for querying/updateing/etc. for a TimeSeries
 */
public interface TimeSeriesService {
    Iterable<TimeSeriesQueryResultModel<TimeSeries.Entry>> queryAll(Collection<TimeSeriesQueryModel> query);

    TimeSeriesModel<TimeSeries.Entry> query(TimeSeriesQueryModel query);

    Iterable<TimeSeriesQueryResultModel<AuditableTimeSeries.AuditableEntry>> auditAll(Collection<TimeSeriesQueryModel> updates);

    TimeSeriesModel<AuditableTimeSeries.AuditableEntry> audit(TimeSeriesQueryModel updates);

    TimeSeriesModel<TimeSeries.Entry> delete(TimeSeriesQueryModel queryModel);

    void deleteAll(List<TimeSeriesQueryModel> deletes);

    void update(List<TimeSeriesUpdateModel> updates);
}
