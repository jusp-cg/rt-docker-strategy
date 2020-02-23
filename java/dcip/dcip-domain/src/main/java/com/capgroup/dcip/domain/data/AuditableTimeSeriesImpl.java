package com.capgroup.dcip.domain.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.NavigableMap;
import java.util.stream.Stream;

public class AuditableTimeSeriesImpl extends TimeSeriesImpl implements AuditableTimeSeries,
        TimeSeries {
    public AuditableTimeSeriesImpl(NavigableMap<LocalDateTime, Entry> items) {
        super(items);
    }

    public AuditableTimeSeriesImpl(Collection<AuditableEntry> entries) {
        super(entries);
    }

    public AuditableTimeSeriesImpl(Stream<AuditableEntry> entries) {
        super(entries);
    }

    @Override
    public void add(LocalDateTime localDateTime, BigDecimal value, LocalDateTime modifiedTimestamp) {
        super.items.put(localDateTime, new AuditableEntry(localDateTime, value, modifiedTimestamp));
    }

    public Collection<AuditableEntry> auditableEntries() {
        return (Collection<AuditableEntry>) (Collection<?>) super.entries();
    }

    @Override
    protected AuditableTimeSeries create(NavigableMap<LocalDateTime, Entry> items) {
        return new AuditableTimeSeriesImpl(items);
    }

    @Override
    protected AuditableEntry createEntry(LocalDateTime localDateTime, BigDecimal value) {
        return new AuditableEntry(localDateTime, value, null);
    }
}
