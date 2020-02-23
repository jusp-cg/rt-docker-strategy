package com.capgroup.dcip.domain.data;

import java.util.EnumSet;
import java.util.stream.Collectors;

public enum SeriesFilter {
    SUMMARY(1),
    ALERTS(2),
    CHARTS(4);

    int value;

    SeriesFilter(int value) {
        this.value = value;
    }

    public static EnumSet<SeriesFilter> fromValue(long value) {
        return EnumSet.copyOf(EnumSet.allOf(SeriesFilter.class).stream().filter(x -> (x.value | value) == value).collect(Collectors.toList()));
    }

    public static long toValue(EnumSet<SeriesFilter> filters) {
        return filters.stream().reduce(0, (l, r) -> l + r.value
                , (l, r) -> l + r);
    }

    public boolean isValidFor(int value) {
        return (value | this.value) == value;
    }
}
