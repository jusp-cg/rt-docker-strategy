package com.capgroup.dcip.domain.data;

import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * A collection of timestamp/values
 */
public interface TimeSeries {
    static TimeSeries of(Entry... entries) {
        return new TimeSeriesImpl(Arrays.asList(entries));
    }

    static TimeSeries of(Collection<Entry> entries) {
        return new TimeSeriesImpl(entries);
    }

    ;

    static TimeSeries of(Stream<Entry> entries) {
        return new TimeSeriesImpl(entries);
    }

    static TimeSeries empty() {
        return new TimeSeriesImpl();
    }

    TimeSeries subSeries(LocalDateTime floor, LocalDateTime ceiling);

    TimeSeries subSeries(LocalDateTimeRange dateRange);

    int size();

    Duration duration();

    LocalDateTime startTimestamp();

    LocalDateTime endTimestamp();

    void add(LocalDateTime timestamp, BigDecimal value);

    Entry get(LocalDateTime timestamp, MatchComparison match);

    Collection<LocalDateTime> timestamps();

    Collection<TimeSeries.Entry> entries();

    default TimeSeries apply(TimeSeriesFunction function) {
        return of(entries().stream().map(x -> function.apply(x)).filter(x -> x != null));
    }

    default Entry get(LocalDateTime timestamp) {
        return get(timestamp, MatchComparison.EXACT);
    }

    default BigDecimal getValue(LocalDateTime timestamp, MatchComparison match) {
        Entry item = get(timestamp, match);
        return item == null ? null : item.getValue();
    }

    default BigDecimal getValue(LocalDateTime timestamp) {
        Entry item = get(timestamp);
        return item == null ? null : item.getValue();
    }

    default LocalDateTimeRange range() {
        return new LocalDateTimeRange(startTimestamp(), endTimestamp());
    }


    enum MatchComparison {
        EXACT, CLOEST, CEILING, FLOOR
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Entry {
        LocalDateTime timestamp;
        BigDecimal value;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
