package com.capgroup.dcip.domain.data;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a date at a particular point in time. Adapts a LocalDateTime to
 * the DateQuery interface
 */
@Data
@AllArgsConstructor
public class AbsoluteDateQuery implements DateQuery {
	LocalDateTime value;

	@Override
	public LocalDateTime toDateTime() {
		return value;
	}
}
