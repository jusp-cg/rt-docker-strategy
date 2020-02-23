package com.capgroup.dcip.domain.canvas;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

/**
 * Meta-data of a WorkbenchResource - describes the workbench resource 
 */
@Entity
@Data
public class WorkbenchResourceType {
	@Id
	@Column(name = "Id")
	private long id;

	@Column(name = "Name")
	private String name;

	@Column(name = "Description")
	private String description;
}
