package com.capgroup.dcip.domain.entity;

import com.capgroup.dcip.domain.common.LocalDateTimeRange;

/**
 * Represents an object with a date range for which it is valid for
 */
public interface Temporal {

    LocalDateTimeRange getValidPeriod();
}
