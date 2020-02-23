package com.capgroup.dcip.domain.canvas;

/***
 * Meta-data for a canvas.
 */
/*
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class CanvasType {
	
	@Id
	private long id;
	
	@OneToMany(fetch = FetchType.LAZY)
	private List<CanvasProfileWorkbenchResource> canvasProfileWorkbenchResources;

	@Getter
	@Column
	private String name;

	public CanvasType(String name) {
		this.name = name;
	}

	public List<WorkbenchResource> getDefaultWorkbenchResources() {
		return canvasProfileWorkbenchResources.stream().filter(item -> item.getProfile() == null)
				.map(item -> item.getWorkbenchResource()).collect(Collectors.toList());
	}

	public List<WorkbenchResource> getWorkbenchResourcesForProfile(Profile profile) {
		return canvasProfileWorkbenchResources.stream().filter(item -> item.getProfile().equals(profile))
				.map(item -> item.getWorkbenchResource()).collect(Collectors.toList());
	}

	public void addDefaultWorkbenchResource(WorkbenchResource workbenchResource) {
		canvasProfileWorkbenchResources.add(new CanvasProfileWorkbenchResource(workbenchResource, this));
	}

	public void addWorkbenchResourceForProfile(WorkbenchResource workbenchResource, Profile profile) {
		canvasProfileWorkbenchResources.add(new CanvasProfileWorkbenchResource(workbenchResource, this, profile));
	}

	public void removeDefaultWorkbenchResource(WorkbenchResource workbenchResource) {
		canvasProfileWorkbenchResources
				.removeIf(item -> item.getWorkbenchResource().equals(workbenchResource) && item.getProfile() == null);
	}

	public void removeWorkbenchResourceForProfile(WorkbenchResource workbenchResource, Profile profile) {
		canvasProfileWorkbenchResources.removeIf(
				item -> item.getWorkbenchResource().equals(workbenchResource) && item.getProfile().equals(profile));
	}
}
*/