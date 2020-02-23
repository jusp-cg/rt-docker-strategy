package com.capgroup.dcip.app.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.capgroup.dcip.app.data.TimeSeriesDataSourceConnectionMatchResult.Feature;

@RunWith(SpringRunner.class)
public class TimeSeriesDataSourceConnectionMatchResultTest {
	TimeSeriesDataSourceConnectionMatchResult result1;
	TimeSeriesDataSourceConnectionMatchResult result2;
	TimeSeriesDataSourceConnectionMatchResult result3;
	TimeSeriesDataSourceConnectionMatchResult result4;
	TimeSeriesDataSourceConnectionMatchResult result5;

	@SuppressWarnings("serial")
	@Before
	public void init() {
		result1 = new TimeSeriesDataSourceConnectionMatchResult(
				TimeSeriesDataSourceConnectionMatchResult.Feature.Series);
		result2 = new TimeSeriesDataSourceConnectionMatchResult(
				TimeSeriesDataSourceConnectionMatchResult.Feature.Identifiers);
		result3 = new TimeSeriesDataSourceConnectionMatchResult(
				new HashMap<TimeSeriesDataSourceConnectionMatchResult.Feature, Boolean>() {
					{
						put(TimeSeriesDataSourceConnectionMatchResult.Feature.Identifiers, Boolean.FALSE);
					}
				});

		result4 = new TimeSeriesDataSourceConnectionMatchResult();
		result5 = new TimeSeriesDataSourceConnectionMatchResult(
				TimeSeriesDataSourceConnectionMatchResult.Feature.Series,
				TimeSeriesDataSourceConnectionMatchResult.Feature.Identifiers);

	}

	@Test
	public void checkValidity() {
		assertTrue(result1.isValid());
		assertTrue(result2.isValid());
		assertFalse(result3.isValid());
		assertTrue(result4.isValid());
	}

	@Test
	public void checkFeatureIsValidTest() {
		assertTrue(result1.isValid(Feature.Series).get());
		assertFalse(result1.isValid(Feature.Identifiers).isPresent());
		assertFalse(result3.isValid(Feature.Identifiers).get());
	}

	@Test
	public void validateOrderTest() {
		List<TimeSeriesDataSourceConnectionMatchResult> l = Arrays.asList(result1, result2, result3, result4, result5);
		l.sort(Comparator.naturalOrder());

		assertSame(result5, l.get(0));
		assertSame(result1, l.get(1));
		assertSame(result2, l.get(2));
		assertSame(result4, l.get(3));
		assertSame(result3, l.get(4));
	}
}
