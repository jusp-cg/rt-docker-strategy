package com.capgroup.dcip.app.canvas;

import com.capgroup.dcip.app.entity.TemporalEntityModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CanvasItemModel extends TemporalEntityModel {
	long canvasId;
	long entityId;
	WorkbenchResourceModel workbenchResource;
}
