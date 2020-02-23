package com.capgroup.dcip.domain.data;

import java.time.LocalDateTime;

import lombok.Getter;

/**
 * Field that defines the type of offset from a point in time e.g. start of month
 */
public enum RelativeDateOffset {
	NONE("") {
		@Override
		public LocalDateTime toOffset(CalendarPeriodType periodType, LocalDateTime dt) {
			return dt;
		}
	},
	START("B") {
		@Override
		public LocalDateTime toOffset(CalendarPeriodType periodType, LocalDateTime dt) {
			return periodType.startOfPeriod(dt);
		}
	},
	END("E") {
		@Override
		public LocalDateTime toOffset(CalendarPeriodType periodType, LocalDateTime dt) {
			return periodType.endOfPeriod(dt);
		}
	};
	
	RelativeDateOffset(String acronym){
		this.acronym = acronym;
	}
	
	@Getter
	private String acronym;

	public abstract LocalDateTime toOffset(CalendarPeriodType periodType, LocalDateTime dt);
}
