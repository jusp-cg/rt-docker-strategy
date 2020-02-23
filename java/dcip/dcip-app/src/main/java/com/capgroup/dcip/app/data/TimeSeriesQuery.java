package com.capgroup.dcip.app.data;

import com.capgroup.dcip.app.reference.company.CompanyModel;

import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.domain.data.Series;
import com.capgroup.dcip.domain.identity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Defines the properties for a query for a TimeSeries
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TimeSeriesQuery extends TimeSeriesCriteria {
	private Integer startOffset;
	private LocalDateTimeRange dateRange;

	public TimeSeriesQuery(LocalDateTimeRange dateRange, Series series,
						   CompanyModel companyModel, User user,
						   Integer startOffset){
		super(series, companyModel, user);
		this.dateRange = dateRange;
		this.startOffset = startOffset;
	}

	public TimeSeriesQuery(LocalDateTimeRange dateRange, Series series,
			   CompanyModel companyModel,
			   Integer startOffset){
		super(series, companyModel, null);
	this.dateRange = dateRange;
	this.startOffset = startOffset;
}


}
