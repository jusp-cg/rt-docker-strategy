package com.capgroup.dcip.app.data;

import com.capgroup.dcip.domain.data.TimeSeries;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesQueryResultModel<T extends TimeSeries.Entry> {
    private TimeSeriesQueryModel query;
    private TimeSeriesModel<T> timeSeries;
    private String errorMessage;

    public TimeSeriesQueryResultModel(TimeSeriesQueryModel query,
                                      TimeSeriesModel<T> timeSeries){
        this.query = query;
        this.timeSeries = timeSeries;
    }

    public TimeSeriesQueryResultModel(TimeSeriesQueryModel query,
                                      String error){
        this.query = query;
        this.errorMessage = error;
    }

}
