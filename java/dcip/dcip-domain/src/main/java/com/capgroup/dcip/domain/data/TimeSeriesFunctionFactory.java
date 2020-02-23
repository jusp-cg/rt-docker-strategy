package com.capgroup.dcip.domain.data;

import java.util.Map;

/**
 * Contract for creating TimeSeriesFunctions 
 */
public interface TimeSeriesFunctionFactory {
	
	String name();
	
	TimeSeriesFunction create(Map<String, Object> parameters);
}