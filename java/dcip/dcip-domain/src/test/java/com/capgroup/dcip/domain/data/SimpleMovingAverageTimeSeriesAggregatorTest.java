package com.capgroup.dcip.domain.data;

import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SimpleMovingAverageTimeSeriesAggregatorTest {
	SimpleMovingAverageTimeSeriesAggregator aggregator;
	int days = 5;

	List<TimeSeries.Entry> entries = Arrays.asList(new TimeSeries.Entry(LocalDateTime.now(), new BigDecimal(1)),
			new TimeSeries.Entry(LocalDateTime.now(), new BigDecimal(2)),
			new TimeSeries.Entry(LocalDateTime.now(), new BigDecimal(3)),
			new TimeSeries.Entry(LocalDateTime.now(), new BigDecimal(4)),
			new TimeSeries.Entry(LocalDateTime.now(), new BigDecimal(5)),
			new TimeSeries.Entry(LocalDateTime.now(), new BigDecimal(6)),
			new TimeSeries.Entry(LocalDateTime.now(), new BigDecimal(7)),
			new TimeSeries.Entry(LocalDateTime.now(), new BigDecimal(8)),
			new TimeSeries.Entry(LocalDateTime.now(), new BigDecimal(9)),
			new TimeSeries.Entry(LocalDateTime.now(), new BigDecimal(10)));

	@Before
	public void init() {
		aggregator = new SimpleMovingAverageTimeSeriesAggregator(days, LocalDateTime.MIN);
	}

	@Test
	public void simpleTest() {
		List<TimeSeries.Entry> results = entries.stream().map(x -> aggregator.apply(x)).collect(Collectors.toList());
		assertNull(results.get(0));
		assertNull(results.get(1));
		assertNull(results.get(2));
		assertNull(results.get(3));
		org.junit.Assert.assertEquals((1 + 2 + 3 + 4 + 5) / 5d, results.get(4).value.doubleValue(), .1);
		org.junit.Assert.assertEquals((2 + 3 + 4 + 5 + 6) / 5d, results.get(5).value.doubleValue(), .1);
		org.junit.Assert.assertEquals((3 + 4 + 5 + 6 + 7) / 5d, results.get(6).value.doubleValue(), .1);
	}
}
