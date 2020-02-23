package com.capgroup.dcip.domain.data;

import java.time.*;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CalendarPeriodTypeTest {

	LocalDateTime testDate = LocalDateTime.of(LocalDate.of(2001, 1, 1),
            LocalTime.of(13, 0, 0));

	@Test
	public void noneCalendarPeriodTypeDoesntChangeStartOfPeriod() {
		LocalDateTime resultDate = CalendarPeriodType.NONE.startOfPeriod(testDate);
		assertEquals(testDate, resultDate);
	}

	@Test
	public void noneCalendarPeriodTypeDoesntChangeEndOfPeriod() {
		LocalDateTime resultDate = CalendarPeriodType.NONE.endOfPeriod(testDate);
		assertEquals(testDate, resultDate);
	}

	@Test
	public void noneCalendarPeriodTypeDoesntChangeAddingPeriods() {
		LocalDateTime resultDate = CalendarPeriodType.NONE.addPeriods(testDate, 1);
		assertEquals(testDate, resultDate);
	}

	@Test
	public void startOfPeriodTest() {
		LocalDateTime resultDate = CalendarPeriodType.DAY.startOfPeriod(testDate);
		assertEquals(LocalDateTime.of(2001, 1, 1, 0, 0, 0), resultDate);
	}

	@Test
	public void endOfPeriodTest() {
		LocalDateTime resultDate = CalendarPeriodType.MONTH.endOfPeriod(testDate);
		assertEquals(LocalDateTime.of(2001, 1, 31, 0, 0, 0), resultDate);
	}

	
	@Test
	public void addDaysTest() {
		LocalDateTime resultDate = CalendarPeriodType.DAY.addPeriods(testDate, 3);
		assertEquals(LocalDateTime.of(2001, 1, 4, 13, 0, 0), resultDate);
	}
	
	

}
