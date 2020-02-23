package com.capgroup.dcip.app.data;

import com.capgroup.dcip.domain.data.TimeSeries;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data transfer object that represents a Timeseries
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSeriesModel<TEntry extends TimeSeries.Entry> {
    List<TEntry> values;
}
