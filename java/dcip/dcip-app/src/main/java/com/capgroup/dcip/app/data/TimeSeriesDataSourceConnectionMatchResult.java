package com.capgroup.dcip.app.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Data;

/**
 * This class represents the results of trying to finding a
 * TimeSeriesDataSourceConnection for a query.
 * <p>
 * The result can be ordered so that the one with the highest match is first in
 * the list. The order of features is defined by the order of the fields in the
 * Enum Feature. The rules are as follows:
 * <li>1. A field is null - the datasource may or may not support the feature
 * <li>2. A field is true - the datasource supports the feature
 * <li>3. A field is false - the datasource does not support the feature
 * 
 * An invalid result is one for which a field has a value of 'false'
 */
@Data
public class TimeSeriesDataSourceConnectionMatchResult
		implements Comparable<TimeSeriesDataSourceConnectionMatchResult> {

	public enum Feature {
		Series, Identifiers
	}

	private Map<Feature, Boolean> features = new HashMap<>();

	public boolean isValid() {
		return !features.containsValue(Boolean.FALSE);
	}

	public TimeSeriesDataSourceConnectionMatchResult(Feature... features) {
		Arrays.asList(features).forEach(x -> this.features.put(x, true));
	}

	public TimeSeriesDataSourceConnectionMatchResult(Map<Feature, Boolean> features) {
		features.keySet().forEach(x -> this.features.put(x, features.get(x)));
	}

	public Optional<Boolean> isValid(Feature feature) {
		return Optional.ofNullable(features.get(feature));
	}

	@Override
	public int compareTo(TimeSeriesDataSourceConnectionMatchResult rhs) {
		// if this is invalid then make sure it is towards the end of the queue
		if (!isValid()) {
			return 1;
		}

		// if this is valid then ensure that it is higher in the queue
		if (!rhs.isValid()) {
			return -1;
		}

		return Arrays.stream(Feature.values()).map(x -> match(isValid(x), rhs.isValid(x))).filter(x -> x.isPresent())
				.map(x -> x.get()).findFirst().orElse(0);
	}

	static <T> Optional<Integer> match(Optional<T> lhs, Optional<T> rhs) {
		if (lhs.isPresent() && !rhs.isPresent()) {
			return Optional.of(-1);
		}
		if (rhs.isPresent() && !lhs.isPresent()) {
			return Optional.of(1);
		}
		return Optional.empty();
	}
}