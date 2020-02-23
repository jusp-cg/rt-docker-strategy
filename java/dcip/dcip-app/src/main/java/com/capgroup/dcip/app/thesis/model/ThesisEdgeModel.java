package com.capgroup.dcip.app.thesis.model;

import com.capgroup.dcip.app.entity.TemporalEntityModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ThesisEdgeModel extends TemporalEntityModel {
	private long thesisId;
	private ThesisPointModel parentThesisPoint;
	private ThesisPointModel childThesisPoint;
}
