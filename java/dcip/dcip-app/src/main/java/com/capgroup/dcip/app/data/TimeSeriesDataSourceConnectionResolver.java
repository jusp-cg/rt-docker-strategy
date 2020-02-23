package com.capgroup.dcip.app.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Contains the logic for determining which TimeSeriesDataSourceConnection is
 * the one to process the Query. More than one TimeSeriesConnectionManager may
 * be applicable to process the result - the class
 * TimeSeriesDataSourceConnectionMatchResult contains the rules for determining
 * the ordering of the results
 */
@Component
public class TimeSeriesDataSourceConnectionResolver {
    private TimeSeriesDataSourceConnectionManager connectionManager;

    @Autowired
    public TimeSeriesDataSourceConnectionResolver(TimeSeriesDataSourceConnectionManager manager) {
        this.connectionManager = manager;
    }

    public Optional<TimeSeriesDataSourceConnection> resolve(TimeSeriesCriteria query) {
        // findAll the data source that is the best match for the query
        Stream<Pair<TimeSeriesDataSourceConnection, TimeSeriesDataSourceConnectionMatchResult>> sortedAndFilteredSources = connectionManager
                .dataSourceConnections().stream().map(source -> {
                    return Pair.of(source, source.matches(query));
                }).filter(x -> x.getSecond().isValid()).sorted(Comparator.comparing(x -> x.getSecond()));

        return sortedAndFilteredSources.findFirst().map(x -> x.getFirst());
    }
}
