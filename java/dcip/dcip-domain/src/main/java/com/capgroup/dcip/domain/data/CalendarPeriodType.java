package com.capgroup.dcip.domain.data;

import lombok.Getter;

import java.time.*;

/**
 * Represents a calendar period (e.g. day/week/month/etc.)
 */
public enum CalendarPeriodType {
    NONE(null, "", "") {
        @Override
        public LocalDateTime startOfPeriod(LocalDateTime dt) {
            return dt;
        }
    },
    DAY(Period.ofDays(1), "Days", "D") {
        @Override
        public LocalDateTime startOfPeriod(LocalDateTime dt) {
            return LocalDateTime.of(LocalDate.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth()), LocalTime.MIDNIGHT);
        }
    },
    MONTH(Period.ofMonths(1), "Mnths", "M") {
        @Override
        public LocalDateTime startOfPeriod(LocalDateTime dt) {
            return LocalDateTime.of(LocalDate.of(dt.getYear(), dt.getMonth(), 1), LocalTime.MIDNIGHT);
        }
    },
    QUARTER(Period.ofMonths(3), "Qtrs", "Q") {
        @Override
        public LocalDateTime startOfPeriod(LocalDateTime dt) {
            return LocalDateTime.of(LocalDate.of(dt.getYear(), dt.getMonth().firstMonthOfQuarter(), 1),
                    LocalTime.MIDNIGHT);
        }
    },
    YEAR(Period.ofYears(1), "Yrs", "Y") {
        @Override
        public LocalDateTime startOfPeriod(LocalDateTime dt) {
            return LocalDateTime.of(LocalDate.of(dt.getYear(), Month.JANUARY, 1), LocalTime.MIDNIGHT);
        }
    };

    private final Period period;
    @Getter
    private String shortName;
    @Getter
    private String acronym;

    CalendarPeriodType(Period period, String shortName, String acronym) {
        this.period = period;
        this.shortName = shortName;
        this.acronym = acronym;
    }

    public abstract LocalDateTime startOfPeriod(LocalDateTime input);

    public LocalDateTime endOfPeriod(LocalDateTime dt) {
        return this == NONE ? dt : startOfPeriod(addPeriods(dt, 1)).minusDays(1);
    }

    public LocalDateTime addPeriods(LocalDateTime dt, int count) {
        return this == NONE ? dt : dt.plus(period.multipliedBy(count));
    }
}