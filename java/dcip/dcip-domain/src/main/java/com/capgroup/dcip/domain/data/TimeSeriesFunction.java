package com.capgroup.dcip.domain.data;

/**
 * A function that can be applied to entry of a timeseries i.e. manipulates the values of a timeseries 
 */
public interface TimeSeriesFunction {
	TimeSeries.Entry apply(TimeSeries.Entry nextTime);
}
