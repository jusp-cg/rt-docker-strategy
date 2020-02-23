package com.capgroup.dcip.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SQLUtil {
    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static String toSQLDateFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_FORMATTER);
    }

    public static String toSQLDateTimeFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }

    public static String toSQLDateTimeFormat(ZonedDateTime localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }
}
