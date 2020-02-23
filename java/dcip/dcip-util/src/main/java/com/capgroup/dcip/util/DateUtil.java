package com.capgroup.dcip.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date utility class for supporting date management
 */
public final class DateUtil {

	private DateUtil() {
	}

	public static String format(TemporalAccessor date) {
		return format(date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	}

	/**
	 * Format date value to string value based on pattern parameter
	 *
	 * @param date
	 *            - LocalDate value
	 * @param datePattern
	 *            - String date pattern
	 * @return String - formated date value
	 */
	public static String format(TemporalAccessor date, String datePattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
		return formatter.format(date);
	}

	/**
	 * Format date time value to string value based on pattern parameter
	 *
	 * @param date
	 *            - LocalDateTime value
	 * @param datePattern
	 *            - String date pattern
	 * @return String - formated date value
	 */
	public static String format(LocalDateTime date, String datePattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
		return date.format(formatter);
	}
}
