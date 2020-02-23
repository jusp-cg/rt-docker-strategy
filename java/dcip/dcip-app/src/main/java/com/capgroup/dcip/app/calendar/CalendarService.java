package com.capgroup.dcip.app.calendar;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

public interface CalendarService {
	LocalDateTime subtractBusinessDays(LocalDateTime timestamp, int count);
}
