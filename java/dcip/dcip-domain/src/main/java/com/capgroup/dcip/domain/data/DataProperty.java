package com.capgroup.dcip.domain.data;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.capgroup.dcip.util.ConverterUtils;

import lombok.Data;

/**
 * Represents a collection of key/value pairs that can be associated with a
 * DataSource/DataSourceConnection or a SeriesMapping
 */
@Entity
@Data
public class DataProperty {
    @Id
    @Column
    long id;

    @Column
    String key;

    @Column
    String value;

//	@ManyToOne(optional = true, fetch = FetchType.LAZY)
//	@JoinColumn(name = "SeriesMappingId")
//	SeriesMapping seriesMapping;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_source_connection_id")
    DataSourceConnection dataSourceConnection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_source_id")
    DataSource dataSource;

    public <T> T getValue(Class<T> type) {
        return ConverterUtils.convertTo(value, type);
    }

    public static Map<String, String> toMap(Stream<DataProperty> properties) {
        return properties.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
    }

    /**
     * Merges two collections of DataProperties. The parameter rhs overwrites the
     * values of parameter lhs
     */
    public static Stream<DataProperty> merge(Stream<DataProperty> lhs, Stream<DataProperty> rhs) {
        Map<String, DataProperty> lhsMap = lhs.collect(Collectors.toMap(x -> x.getKey(), x -> x));
        lhsMap.putAll(rhs.collect(Collectors.toMap(x -> x.getKey(), x -> x)));
        return lhsMap.values().stream();
    }
}
