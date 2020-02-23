package com.capgroup.dcip.app.canvas_summary;

import com.capgroup.dcip.app.entity.TemporalEntityModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CanvasSummaryModel extends TemporalEntityModel {
	private String tagline;
	private String action;
	private long canvasId;
}
