package com.capgroup.dcip.domain.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Represents a source of TimeSeries data e.g. S&P
 */
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class DataSource {

	@Id
	@Getter
	@Column
	private long id;

	@Column
	private String name;

	@Column
	private String description;

	@OneToMany(mappedBy = "dataSource", fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	private Set<DataProperty> properties;

	public DataSource(long id) {
		properties = new HashSet<>();
		this.id = id;
	}
	
	public Stream<DataProperty> resolveProperties() {
		return properties.stream();
	}
}
