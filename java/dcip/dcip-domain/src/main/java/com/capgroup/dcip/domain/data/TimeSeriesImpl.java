package com.capgroup.dcip.domain.data;

import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.util.stream.MoreCollectors;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Implementation of a TimeSeries. This class is not instantiate directly - but
 * by the static TimeSeries.of methods
 */
class TimeSeriesImpl implements TimeSeries {

    NavigableMap<LocalDateTime, Entry> items;

    TimeSeriesImpl() {
        this(new TreeMap<>());
    }

    TimeSeriesImpl(NavigableMap<LocalDateTime, Entry> items) {
        this.items = items;
    }

    TimeSeriesImpl(Collection<? extends Entry> entries) {
        this(entries.stream());
    }

    TimeSeriesImpl(Stream<? extends Entry> entries) {
        this(entries.collect(MoreCollectors.toTreeMap(Entry::getTimestamp, Function.identity())));
    }

    @Override
    public Duration duration() {
        LocalDateTime start = startTimestamp();
        LocalDateTime end = endTimestamp();

        return Duration.between(start == null ? LocalDateTime.MIN : start, end == null ? LocalDateTime.MAX : end);
    }

    @Override
    public TimeSeries subSeries(LocalDateTime floor, LocalDateTime ceiling) {
        return create(items.subMap(floor, true, ceiling, true));
    }

    @Override
    public TimeSeries subSeries(LocalDateTimeRange dateRange) {
        return subSeries(dateRange.getStart(), dateRange.getEnd());
    }

    @Override
    public LocalDateTime startTimestamp() {
        return items.firstKey();
    }

    @Override
    public LocalDateTime endTimestamp() {
        return items.lastKey();
    }

    @Override
    public void add(LocalDateTime timestamp, BigDecimal value) {
        items.put(timestamp, createEntry(timestamp, value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Entry get(LocalDateTime timestamp, MatchComparison match) {
        Map.Entry<LocalDateTime, ? extends TimeSeries.Entry> value = null;
        switch (match) {
            case EXACT:
                value = (Map.Entry<LocalDateTime, ? extends TimeSeries.Entry>) items.get(timestamp);
                break;
            case FLOOR:
                value = items.floorEntry(timestamp);
                break;
            case CEILING:
                value = items.ceilingEntry(timestamp);
                break;
            case CLOEST:
                Map.Entry<LocalDateTime, ? extends TimeSeries.Entry> lowest = items.floorEntry(timestamp);
                Map.Entry<LocalDateTime, ? extends TimeSeries.Entry> highest = items.ceilingEntry(timestamp);

                if (lowest == null) {
                    value = highest;
                    break;
                }
                if (highest == null) {
                    value = lowest;
                    break;
                }

                long floorDiff =
                        timestamp.toEpochSecond(ZoneOffset.UTC) - lowest.getKey().toEpochSecond(ZoneOffset.UTC);
                long ceilingDiff =
                        highest.getKey().toEpochSecond(ZoneOffset.UTC) - timestamp.toEpochSecond(ZoneOffset.UTC);

                value = floorDiff < ceilingDiff ? lowest : highest;
        }
        return value == null ? null : value.getValue();
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public Collection<LocalDateTime> timestamps() {
        return items.navigableKeySet();
    }

    @Override
    public Collection<Entry> entries() {
        return this.items.values();
    }

    protected Entry createEntry(LocalDateTime localDateTime, BigDecimal value) {
        return new Entry(localDateTime, value);
    }

    protected TimeSeries create(NavigableMap<LocalDateTime, Entry> items) {
        return new TimeSeriesImpl(items);
    }
}
