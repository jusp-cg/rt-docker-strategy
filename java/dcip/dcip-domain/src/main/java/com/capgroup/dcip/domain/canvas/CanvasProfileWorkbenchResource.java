package com.capgroup.dcip.domain.canvas;

/**
 * Profile specific configuration of a canvas. Defines the default workbench resources for a type of canvas 
 */

/*
@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CanvasProfileWorkbenchResource extends TemporalEntity {
	/**
	 * Generated Serial Version Id
	 */
	/*private static final long serialVersionUID = 6001481522491085655L;

	@ManyToOne
	@JoinColumn(name = "profileId")
	private Profile profile;

	@ManyToOne(optional = false)
	@NotNull
	@JoinColumn(name = "workbenchResourceId")
	private WorkbenchResource workbenchResource;

	@ManyToOne(optional = false)
	@NotNull
	@JoinColumn(name = "canvasTypeId")
	private CanvasType canvasType;

	public CanvasProfileWorkbenchResource(WorkbenchResource workspaceResource, CanvasType canvasType) {
		this.workbenchResource = workspaceResource;
		this.canvasType = canvasType;
	}

	public CanvasProfileWorkbenchResource(WorkbenchResource workspaceResource, CanvasType canvasType, Profile profile) {
		this.profile = profile;
		this.canvasType = canvasType;
		this.workbenchResource = workspaceResource;
	}
}*/