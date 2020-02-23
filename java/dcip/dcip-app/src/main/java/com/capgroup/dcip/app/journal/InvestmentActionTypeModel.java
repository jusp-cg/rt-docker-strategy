package com.capgroup.dcip.app.journal;

import com.capgroup.dcip.app.entity.TemporalEntityModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvestmentActionTypeModel extends TemporalEntityModel {
	private String investmentAction;
	private Boolean enableEmail;
	private String groupName;
}
