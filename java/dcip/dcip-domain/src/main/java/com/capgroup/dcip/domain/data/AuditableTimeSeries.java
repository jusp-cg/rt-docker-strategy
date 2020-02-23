package com.capgroup.dcip.domain.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public interface AuditableTimeSeries extends TimeSeries {

    static AuditableTimeSeries of(AuditableEntry... entries) {
        return new AuditableTimeSeriesImpl(Arrays.asList(entries));
    }

    static AuditableTimeSeries of(Collection<AuditableEntry> entries) {
        return new AuditableTimeSeriesImpl(entries);
    }

    static AuditableTimeSeries of(Stream<AuditableEntry> entries) {
        return new AuditableTimeSeriesImpl(entries);
    }

    void add(LocalDateTime localDateTime, BigDecimal value, LocalDateTime modifiedTimestamp);

    Collection<AuditableEntry> auditableEntries();

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    class AuditableEntry extends Entry {

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private LocalDateTime modifiedTimestamp;

        public AuditableEntry(LocalDateTime timestamp, BigDecimal value,
                              LocalDateTime modifiedTimestamp) {
            super(timestamp, value);
            this.modifiedTimestamp = modifiedTimestamp;
        }
    }
}
