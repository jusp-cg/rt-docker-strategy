package com.capgroup.dcip.domain.data;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Factory responsible for creating a SimpleMovingAverageAggregator
 */
@Component
public class SimpleMovingAverageTimeSeriesAggregatorFactory implements TimeSeriesFunctionFactory {

	public String name() {
		return "SimpleMovingAverageTimeSeriesAggregator";
	}

	@Override
	public TimeSeriesFunction create(Map<String, Object> parameters) {
		return new SimpleMovingAverageTimeSeriesAggregator((int) parameters.get("dayCount"),
				(LocalDateTime)parameters.get("startDate"));
	}
}
