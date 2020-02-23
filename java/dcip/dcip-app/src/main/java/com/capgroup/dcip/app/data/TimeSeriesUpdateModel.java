package com.capgroup.dcip.app.data;

import com.capgroup.dcip.domain.data.TimeSeries;
import lombok.Data;

@Data
public class TimeSeriesUpdateModel {
    TimeSeriesCriteriaModel timeSeries;
    TimeSeriesModel<TimeSeries.Entry> data;
}
