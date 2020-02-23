package com.capgroup.dcip.app.journal;

import com.capgroup.dcip.app.entity.TemporalEntityModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvestmentPromptModel extends TemporalEntityModel {
	boolean isPublic;
	String comment;
}
