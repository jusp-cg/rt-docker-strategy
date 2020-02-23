package com.capgroup.dcip.app.data;

import java.util.Collection;

/**
 * Manages the TimeSeriesDataSourceConnections
 */
public interface TimeSeriesDataSourceConnectionManager {
	Collection<TimeSeriesDataSourceConnection> dataSourceConnections();
}
