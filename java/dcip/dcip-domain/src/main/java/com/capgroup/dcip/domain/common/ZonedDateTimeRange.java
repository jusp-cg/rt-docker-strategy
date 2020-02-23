package com.capgroup.dcip.domain.common;

import com.capgroup.dcip.util.Range;
import com.capgroup.dcip.util.RangeMerger;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.*;
import java.util.stream.Stream;

/***
 * Represents a period in time. By default the end date is 31 dec 9999
 */
@Embeddable
@Data
public class ZonedDateTimeRange implements Range<ZonedDateTime>, Serializable {
    private static final long serialVersionUID = 2739850369368326152L;

    public static ZonedDateTime MAX_END = ZonedDateTime.of(LocalDate.of(9999, 12, 31),
            LocalTime.MIDNIGHT, ZoneId.of("UTC"));
    public static ZonedDateTime MIN_START = ZonedDateTime.of(LocalDate.of(1900, 1, 1),
            LocalTime.MIDNIGHT, ZoneId.of("UTC"));
    private ZonedDateTime start;
    private ZonedDateTime end;

    public ZonedDateTimeRange(ZonedDateTime start) {
        this(start, MAX_END);
    }

    public ZonedDateTimeRange(ZonedDateTime start, ZonedDateTime end) {
        this.start = start;
        this.end = end;
    }

    public ZonedDateTimeRange() {
        this(ZonedDateTime.now(ZoneOffset.UTC));
    }

    /**
     * Merges date ranges together
     */
    public static Stream<ZonedDateTimeRange> merge(Stream<ZonedDateTimeRange> input) {
        return new RangeMerger<ZonedDateTime, ZonedDateTimeRange>((start, end) -> new ZonedDateTimeRange(start, end)).merge(input);
    }

    @Override
    @JsonProperty("startDate")
    public ZonedDateTime getStart() {
        return start;
    }

    @Override
    @JsonProperty("endDate")
    public ZonedDateTime getEnd() {
        return end;
    }

    public LocalDateTimeRange toLocalDateTimeRange(){
        return new LocalDateTimeRange(start.toLocalDateTime(), end.toLocalDateTime());
    }
}
