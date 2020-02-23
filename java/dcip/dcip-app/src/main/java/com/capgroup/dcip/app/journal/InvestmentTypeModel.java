package com.capgroup.dcip.app.journal;

import com.capgroup.dcip.app.entity.TemporalEntityModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvestmentTypeModel extends TemporalEntityModel {
	private String name;
	private String description;
	private boolean defaultFlag;
	private boolean activeFlag;
}
