package com.capgroup.dcip.domain.common;

import com.capgroup.dcip.util.Range;
import com.capgroup.dcip.util.RangeMerger;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

/***
 * Represents a period in time. By default the end date is 31 dec 9999
 */
@Embeddable
@Data
public class LocalDateTimeRange implements Range<LocalDateTime>, Serializable {
    private static final long serialVersionUID = 2739850369368326152L;

    public static LocalDateTime MAX_END = LocalDateTime.of(9999, 12, 31, 0, 0, 0, 0);
    public static LocalDateTime MIN_START = LocalDateTime.of(1900, 1, 1, 0, 0, 0, 0);
    private LocalDateTime start;
    private LocalDateTime end;

    public LocalDateTimeRange(LocalDateTime start) {
        this(start, MAX_END);
    }

    public LocalDateTimeRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTimeRange() {
        this(LocalDateTime.now(ZoneOffset.UTC));
    }

    /**
     * Merges date ranges together
     */
    public static Stream<LocalDateTimeRange> merge(Stream<LocalDateTimeRange> input) {
        return new RangeMerger<LocalDateTime, LocalDateTimeRange>((start, end) -> new LocalDateTimeRange(start, end)).merge(input);
    }

    @Override
    @JsonProperty("startDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    public LocalDateTime getStart() {
        return start;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Override
    @JsonProperty("endDate")
    public LocalDateTime getEnd() {
        return end;
    }

    public ZonedDateTimeRange toZonedDateTimeRange(ZoneId zoneId) {
        return new ZonedDateTimeRange(ZonedDateTime.of(start, zoneId),
                ZonedDateTime.of(end, zoneId));
    }

    public ZonedDateTimeRange toZonedDateTimeRange() {
        return toZonedDateTimeRange(ZoneId.of("UTC"));
    }
}
