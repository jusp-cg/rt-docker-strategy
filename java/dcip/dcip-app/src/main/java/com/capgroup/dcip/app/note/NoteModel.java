package com.capgroup.dcip.app.note;

import com.capgroup.dcip.app.entity.TemporalEntityModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NoteModel extends TemporalEntityModel {
	private String summary;
	private String detail;
	private long canvasId;
}
