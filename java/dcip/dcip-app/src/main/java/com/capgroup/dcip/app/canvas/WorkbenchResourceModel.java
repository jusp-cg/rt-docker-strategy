package com.capgroup.dcip.app.canvas;

import com.capgroup.dcip.domain.canvas.WorkbenchResourceType;

import lombok.Data;

@Data
public class WorkbenchResourceModel {
	private long id;
	private String name;
	private String description;
	private String icon;
	private WorkbenchResourceType workbenchResourceType;
}
