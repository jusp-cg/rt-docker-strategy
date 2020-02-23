package com.capgroup.dcip.domain.data;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Defines the logical connection to the database and the the template query
 */
@Entity(name = "data_source_connection")
@Getter
@NoArgsConstructor
public class DataSourceConnection {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "data_source_id", nullable = true)
	private DataSource dataSource;

	@Id
	@Column
	private long id;

	@Column
	private String connectionName;

	@Column
	private String description;

	@Column
	@Setter
	private String queryString;

	@Column
	private String updatesString;

	@Column
	private String connectionType;

	@OneToMany(mappedBy = "dataSourceConnection", fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	private Set<DataProperty> properties;

	@OneToMany(mappedBy = "dataSourceConnection", fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	private Set<SeriesMapping> series;

	public DataSourceConnection(long id, DataSource dataSource) {
		this.id = id;
		series = new HashSet<>();
		properties = new HashSet<>();
		this.dataSource = dataSource;
	}
	
	public Stream<DataProperty> resolveProperties() {
		return DataProperty.merge(dataSource.resolveProperties(), properties.stream());
	}

	public void addSeries(SeriesMapping seriesMapping) {
		series.add(seriesMapping);
		seriesMapping.setDataSourceConnection(this);
	}

	public boolean isSeriesSupported(Series series) {
		return this.series.stream().anyMatch(x -> x.getSeries().equals(series));
	}

	public boolean isSeriesSupported(long seriesId) {
		return series.stream().anyMatch(x -> x.getSeries().getId() == seriesId);
	}

	public Optional<SeriesMapping> getSeriesInDataSource(Series series) {
		return this.series.stream().filter(x -> x.getSeries().equals(series)).findFirst();
	}
}
