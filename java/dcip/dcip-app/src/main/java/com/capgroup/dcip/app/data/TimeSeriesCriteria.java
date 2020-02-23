package com.capgroup.dcip.app.data;

import com.capgroup.dcip.app.reference.company.CompanyModel;
import com.capgroup.dcip.domain.data.Series;
import com.capgroup.dcip.domain.identity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSeriesCriteria {
	private Series series;
    private CompanyModel company;
    private User user;
}
