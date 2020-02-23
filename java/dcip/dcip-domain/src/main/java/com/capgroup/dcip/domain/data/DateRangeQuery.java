package com.capgroup.dcip.domain.data;

import com.capgroup.dcip.domain.common.LocalDateTimeRange;

import com.capgroup.dcip.domain.common.ZonedDateTimeRange;
import lombok.Data;

/**
 * A query that has a start date and an end date. Either the start or the end
 * date can be null
 */
@Data
public class DateRangeQuery {
	private DateQuery startDate;
	private DateQuery endDate;

	public LocalDateTimeRange toDateRange() {
		return new LocalDateTimeRange(startDate == null ? null : startDate.toDateTime(),
				endDate == null ? null : endDate.toDateTime());
	}
}
