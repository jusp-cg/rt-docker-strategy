package com.capgroup.dcip.app.calendar;

import java.time.LocalDateTime;
import java.time.Period;

import org.springframework.stereotype.Service;

@Service
public class CalendarServiceImpl implements CalendarService {

	/**
	 * This is a bad algorithm - need a proper calendar. More that likely we will
	 * subtract more days than required - but for where this code is used that is
	 * currently acceptable. It assumes 10 days a year the markets aren't open
	 */
	@Override
	public LocalDateTime subtractBusinessDays(LocalDateTime timestamp, int count) {
		int noWeeks = (int)Math.ceil(count/7d);
		int noYears = (int)Math.ceil(count/365d);
		
		return timestamp.minus(Period.of(0, 0, 100 + count + (noWeeks * 2) + (noYears * 10)));
	}
}
