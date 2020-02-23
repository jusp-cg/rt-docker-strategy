package com.capgroup.dcip.domain.data;

import javax.persistence.*;

import com.capgroup.dcip.domain.identity.Identifiable;
import lombok.*;

import java.util.EnumSet;
import java.util.Set;

/**
 * Represents a time series e.g. open price/close price/etc. 
 * The Series can be ordered
 */
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class Series implements Comparable<Series>, Identifiable {
	@Id
	@Column(name = "Id")
	private Long id;
	@Column(name = "Name")
	private String name;
	@Column(name = "Description")
	private String description;
	@Column(name = "OrderBy")
	int orderBy;
    @Column(name = "SeriesInUse")
    int seriesInUse;
	@OneToMany(mappedBy = "series")
	Set<SeriesSchedule> schedules;

	public Series(long id) {
		this.id = id;
	}

	@Override
	public int compareTo(Series o) {
		return Integer.compare(orderBy, o.orderBy);
	}

	public boolean isValidForFilter(SeriesFilter filter) {
		return filter.isValidFor(seriesInUse);
	}

	public boolean isValidForFilters(EnumSet<SeriesFilter> filters) {
		return filters.stream().anyMatch(x->x.isValidFor(seriesInUse));
	}
}
