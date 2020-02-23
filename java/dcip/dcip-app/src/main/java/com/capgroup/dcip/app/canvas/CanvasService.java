package com.capgroup.dcip.app.canvas;

import java.util.Optional;

import com.capgroup.dcip.domain.canvas.WorkbenchResource.WorkbenchResourceId;

/**
 * Methods for manipulating a Canvas
 * <li>Adding/removing/querying workbench resources
 * <li>Adding/removing canvas/querying items
 */
public interface CanvasService {
	CanvasModel createCanvas(CanvasModel model);

	CanvasModel updateCanvas(long id, CanvasModel model);

	Iterable<CanvasModel> findAll(String match, Long profileId);

	CanvasModel findCanvasById(long id);

	void deleteCanvas(long id);

	Iterable<WorkbenchResourceModel> setResourcesForCanvas(long canvasId, Iterable<WorkbenchResourceModel> models);

	Iterable<WorkbenchResourceModel> updateResourcesForCanvas(long canvasId, Iterable<WorkbenchResourceModel> models);

	CanvasModel addCanvasItem(long canvasId, WorkbenchResourceId workbenchResourceId, long entityId);

	Optional<CanvasModel> findByCanvasItemEntityId(WorkbenchResourceId workbenchResourceId, long entityId);

	CanvasModel deleteCanvasItem(WorkbenchResourceId workbenchResourceId, long entityId);

	long canvasItemsCount(long canvasId, long workbenchResourceId);
	
	Iterable<CanvasItemModel> canvasItemsForCanvasAndWorkbenchResource(long canvasId, long workbenchResourceId);

	Iterable<CanvasItemModel> removeWorkbenchResourceForCanvas(long canvasId, long workbenchResourceId);
}
