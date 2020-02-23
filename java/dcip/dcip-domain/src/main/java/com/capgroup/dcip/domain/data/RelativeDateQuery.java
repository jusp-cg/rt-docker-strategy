package com.capgroup.dcip.domain.data;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a query relative to a point in time (e.g. 3 months ago)
 */
@Data
@NoArgsConstructor
public class RelativeDateQuery implements DateQuery {
	private CalendarPeriodType calendarPeriodType;
	private int count;
	private RelativeDateOffset offSet = RelativeDateOffset.NONE;
	private LocalDateTime relativeTo = LocalDateTime.now();

	public RelativeDateQuery(CalendarPeriodType periodType, int count) {
		this.calendarPeriodType = periodType;
		this.count = count;
	}

	@Override
	public LocalDateTime toDateTime() {
		LocalDateTime result = calendarPeriodType.addPeriods(relativeTo, count);
		return offSet.toOffset(calendarPeriodType, result);
	}
}
