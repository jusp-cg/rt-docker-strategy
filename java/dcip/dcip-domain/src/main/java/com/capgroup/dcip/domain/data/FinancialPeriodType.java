package com.capgroup.dcip.domain.data;

/**
 * TBD: Not required at the moment 
 */
public enum FinancialPeriodType {
	NONE(false), FISCAL_YEAR_REL(true), CALENDAR_YEAR_REL(true), FISCAL_QUARTER_REL(true), CALENDAR_QUARTER_REL(
			true), LAST_TWELVE_MONTHS_REL(true), NEXT_TWELVE_MONTHS_REL(true), YEAR_TO_DATE_REL(true), FISCAL_YEAR_ABS(
					false), CALENDAR_YEAR_ABS(false), FISCAL_QUARTER_ABS(false), CALENDAR_QUARTER_ABS(false);

	FinancialPeriodType(boolean rel) {
		this.relative = rel;
	}

	private boolean relative;

	public boolean isRelative() {
		return relative;
	}
}
