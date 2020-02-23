package com.capgroup.dcip.app.data;

import com.capgroup.dcip.domain.data.DataSourceConnection;

/**
 * Creates a TimeSeriesDataSourceConnection from a DataSourceConnection 
 */
public interface TimeSeriesDataSourceConnectionFactory {
	TimeSeriesDataSourceConnection create(DataSourceConnection datasourceConnection);
}
