package com.capgroup.dcip.domain.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;

import com.capgroup.dcip.domain.data.TimeSeries.Entry;

/**
 * Algorithm for calculating the simple moving average
 */
public class SimpleMovingAverageTimeSeriesAggregator implements TimeSeriesFunction {
	int days;
	BigDecimal currentSum = BigDecimal.ZERO;
	LocalDateTime startDate;
	LinkedList<TimeSeries.Entry> entries = new LinkedList<TimeSeries.Entry>();

	public SimpleMovingAverageTimeSeriesAggregator(int days, LocalDateTime startDate) {
		this.days = days;
		this.startDate = startDate;
	}

	@Override
	public Entry apply(Entry nextEntry) {
		// add the entry and increment the sum
		entries.addLast(nextEntry);
		currentSum = currentSum.add(nextEntry.value);

		// if the number of entries exceeds the required limit - remove one
		if (entries.size() > days) {
			TimeSeries.Entry firstEntry = entries.removeFirst();
			currentSum = currentSum.subtract(firstEntry.getValue());
		}

		if (entries.size() == days && (startDate == null || startDate.compareTo(nextEntry.getTimestamp()) <= 0)) {
			// calculate the SMA if the total entries are correct
			return new Entry(nextEntry.getTimestamp(), currentSum.divide(BigDecimal.valueOf(days)));
		}

		return null;
	}
}
