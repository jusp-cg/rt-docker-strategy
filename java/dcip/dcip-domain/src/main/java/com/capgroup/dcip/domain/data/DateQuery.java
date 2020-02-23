package com.capgroup.dcip.domain.data;

import java.time.LocalDateTime;

/**
 * A query for a date. A date is either an absolute date i.e. a specific point
 * in time, or relative to another date (e.g. 1 year prior)
 */
public interface DateQuery {
	LocalDateTime toDateTime();
}
