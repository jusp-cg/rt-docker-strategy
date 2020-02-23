package com.capgroup.dcip.domain.data;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Series/DataSourceConnection specific properties 
 */
@Entity
@Data
@NoArgsConstructor
public class SeriesMapping {
	@Id
	@Column(name ="Id")
	private long Id;
	
	@ManyToOne
	@JoinColumn(name="SeriesId", nullable=false)
	private Series series;
	
	@ManyToOne
	@JoinColumn(name="DataSourceConnectionId", nullable=false)
	private DataSourceConnection dataSourceConnection;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name="SeriesMappingId")
	private Set<DataProperty> properties;
		
	public Stream<DataProperty> resolveProperties(){
		return DataProperty.merge(dataSourceConnection.resolveProperties(), properties.stream());
	}
	
	public SeriesMapping(Series series) {
		this.series = series;
		this.properties = new HashSet<DataProperty>();
		
	}
}
