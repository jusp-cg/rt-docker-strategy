package com.capgroup.dcip.domain.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import com.capgroup.dcip.domain.entity.TemporalEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents q query for a timeseries that a user has saved. The class is temporal since it can be modified
 */
@Entity(name="data_source_query_definition_view")
@Data
@EqualsAndHashCode(callSuper=true)
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call data_source_query_definition_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call data_source_query_definition_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call data_source_query_definition_delete(?, ?)}")
public class DataSourceQueryDefinition extends TemporalEntity{
	/**
	 * Generated Serial Version No
	 */
	private static final long serialVersionUID = 8423664770205130379L;
	
	@Column(nullable=false)
	long companyId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="series_id", nullable=false)
	Series series;
		
	@Column
	String startDate;
	
	@Column
	String endDate;
	
	@Column(nullable=false)
	String name;
}
