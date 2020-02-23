package com.capgroup.dcip.app.data;

import com.capgroup.dcip.domain.data.AuditableTimeSeries;
import com.capgroup.dcip.domain.data.TimeSeries;
import org.mapstruct.Mapper;

import java.util.stream.Collectors;

/**
 * Maps a TimeSeries/Query to a TimeSeriesModel
 */
@Mapper
public interface TimeSeriesMapper {
    default TimeSeriesModel<TimeSeries.Entry> map(TimeSeries timeSeries) {
        return new TimeSeriesModel<>(timeSeries.entries().stream().collect(Collectors.toList()));
    }

    default TimeSeriesModel<AuditableTimeSeries.AuditableEntry> map(AuditableTimeSeries timeSeries) {
        return new TimeSeriesModel<>(timeSeries.auditableEntries().stream().collect(Collectors.toList()));
    }
}
