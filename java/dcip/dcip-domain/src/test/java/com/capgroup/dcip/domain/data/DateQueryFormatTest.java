package com.capgroup.dcip.domain.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class DateQueryFormatTest {
    DateQueryFormat dateQueryFormat = new DateQueryFormat();

    @Test
    public void parseDate() {
        assertEquals(new AbsoluteDateQuery(LocalDateTime.of(LocalDate.of(2010, 10, 23),
                LocalTime.MIDNIGHT)),
                dateQueryFormat.parse("2010-10-23"));
    }

    @Test
    public void parseRelativeDate() {
        RelativeDateQuery result = (RelativeDateQuery) dateQueryFormat.parse("-1Yrs");
        assertEquals(CalendarPeriodType.YEAR, result.getCalendarPeriodType());
        assertEquals(-1, result.getCount());
        assertEquals(RelativeDateOffset.NONE, result.getOffSet());
    }

    @Test
    public void parseRelativeDateWithS() {
        RelativeDateQuery result = (RelativeDateQuery) dateQueryFormat.parse("-10Yrs B");
        assertEquals(CalendarPeriodType.YEAR, result.getCalendarPeriodType());
        assertEquals(-10, result.getCount());
        assertEquals(RelativeDateOffset.START, result.getOffSet());
    }

    @Test
    public void parseBOY() {
        RelativeDateQuery result = (RelativeDateQuery) dateQueryFormat.parse("BOY");
        assertEquals(CalendarPeriodType.YEAR, result.getCalendarPeriodType());
        assertEquals(0, result.getCount());
        assertEquals(RelativeDateOffset.START, result.getOffSet());
    }

    @Test
    public void parseBOM() {
        RelativeDateQuery result = (RelativeDateQuery) dateQueryFormat.parse("BOM");
        assertEquals(CalendarPeriodType.MONTH, result.getCalendarPeriodType());
        assertEquals(0, result.getCount());
        assertEquals(RelativeDateOffset.START, result.getOffSet());
    }

    @Test
    public void parseEOY() {
        RelativeDateQuery result = (RelativeDateQuery) dateQueryFormat.parse("EOY");
        assertEquals(CalendarPeriodType.YEAR, result.getCalendarPeriodType());
        assertEquals(0, result.getCount());
        assertEquals(RelativeDateOffset.END, result.getOffSet());
    }

}
