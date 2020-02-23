package com.capgroup.dcip.domain.data;

import com.microsoft.sqlserver.jdbc.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts a string to a DateQuery (and vice-versa) The format is either a an
 * absolute date e.g. 2010-2-3 or a relative date e.g. -1Yrs/BOM/EOM/etc.
 */
public class DateQueryFormat {

    private static Iterable<CharSequence> CALENDAR_SHORTNAMES = Arrays
            .stream(CalendarPeriodType.values(), 1, CalendarPeriodType.values().length).map(x -> x.getShortName())
            .collect(Collectors.toList());
    private static Iterable<CharSequence> CALENDAR_ACRONYMS = Arrays
            .stream(CalendarPeriodType.values(), 1, CalendarPeriodType.values().length).map(x -> x.getAcronym())
            .collect(Collectors.toList());
    private static Iterable<CharSequence> CALENDAR_OFFSET_ACRONYMS = Arrays.stream(RelativeDateOffset.values())
            .map(x -> x.getAcronym()).collect(Collectors.toList());
    private static Pattern pattern = Pattern.compile("(-?\\d*)?(" + String.join("|", CALENDAR_SHORTNAMES) + ")(\\s(?:"
            + String.join("|", CALENDAR_OFFSET_ACRONYMS) + "))?", Pattern.CASE_INSENSITIVE);
    private static Pattern acronymPattern = Pattern.compile(
            "(" + String.join("|", CALENDAR_OFFSET_ACRONYMS) + ")O(" + String.join("|", CALENDAR_ACRONYMS) + ")");

    Optional<DateQuery> parseDate(String str) {
        try {
            return Optional.of(new AbsoluteDateQuery(
                    LocalDateTime.of(LocalDate.parse(str, DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIDNIGHT)));
        } catch (DateTimeParseException e) {
        }
        return Optional.empty();
    }

    Optional<DateQuery> parseDateTime(String str) {
        try {
            return Optional.of(new AbsoluteDateQuery(
                    LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        } catch (DateTimeParseException e) {
        }
        return Optional.empty();
    }

    Optional<DateQuery> parseAcronym(String str) {
        Matcher matcher = acronymPattern.matcher(str);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        RelativeDateQuery result = new RelativeDateQuery();
        result.setCount(0);

        // set the offset
        String offsetMatcher = matcher.group(1);
        if (!StringUtils.isEmpty(offsetMatcher)) {
            String offset = offsetMatcher.trim();
            result.setOffSet(Arrays.stream(RelativeDateOffset.values())
                    .filter(x -> x.getAcronym().equalsIgnoreCase(offset)).findFirst().get());
        }

        // set the period
        // set the calendar period
        String calendarPeriod = matcher.group(2);
        result.setCalendarPeriodType(Arrays.stream(CalendarPeriodType.values())
                .filter(x -> x.getAcronym().equalsIgnoreCase(calendarPeriod)).findFirst().get());

        return Optional.of(result);
    }

    Optional<DateQuery> parseShortName(String str) {
        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches()) {
            return Optional.empty();
        }
        RelativeDateQuery result = new RelativeDateQuery();

        // set the count
        String countString = matcher.group(1);
        if (!StringUtils.isEmpty(countString)) {
            result.setCount(Integer.parseInt(countString));
        }

        // set the calendar period
        String calendarPeriod = matcher.group(2);
        result.setCalendarPeriodType(Arrays.stream(CalendarPeriodType.values())
                .filter(x -> x.getShortName().equalsIgnoreCase(calendarPeriod)).findFirst().get());

        // set the offset
        String offsetMatcher = matcher.group(3);
        if (!StringUtils.isEmpty(offsetMatcher)) {
            String offset = offsetMatcher.trim();
            result.setOffSet(Arrays.stream(RelativeDateOffset.values())
                    .filter(x -> x.getAcronym().equalsIgnoreCase(offset)).findFirst().get());
        }

        return Optional.of(result);

    }

    Stream<Function<String, Optional<DateQuery>>> parsers() {
        return Stream.of(this::parseDateTime, this::parseDate, this::parseAcronym, this::parseShortName);
    }

    public DateQuery parse(String str) {
        return parsers().map(x -> x.apply(str)).filter(x -> x.isPresent()).findFirst().get()
                .orElseThrow(() -> new RuntimeException("Failed to parse input string:" + str));
    }

    public String toString(DateQuery query) {
        if (query instanceof AbsoluteDateQuery) {
            return ((AbsoluteDateQuery) query).getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        RelativeDateQuery relativeDateQuery = (RelativeDateQuery) query;
        StringBuilder result = new StringBuilder();
        if (relativeDateQuery.getCount() != 0) {
            result.append(relativeDateQuery.getCount());
        }
        result.append(relativeDateQuery.getCalendarPeriodType().getShortName());
        result.append(" ");
        result.append(relativeDateQuery.getOffSet().getAcronym());
        return result.toString().trim();
    }
}
