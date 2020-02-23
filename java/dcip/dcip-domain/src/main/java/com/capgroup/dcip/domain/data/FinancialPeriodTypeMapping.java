package com.capgroup.dcip.domain.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.poi.ss.formula.functions.Na;

/**
 * TBD: Not required at the moment 
 */
@Entity
@Getter
@EqualsAndHashCode(of="id")
public class FinancialPeriodTypeMapping {
	@Id
	@Column(name = "Id")
	private long id;
	
	@Column(name = "FinancialPeriodType")
	@Enumerated(EnumType.ORDINAL)
	private FinancialPeriodType financialPeriodType;
	
	@Column(name = "SourceFinancialPeriodTypeId")
	private String sourceFinancialPeriodTypeId;
	
	@ManyToOne
	@JoinColumn(name="DataSourceConnectionId")
	private DataSourceConnection dataSourceConnection; 
}
