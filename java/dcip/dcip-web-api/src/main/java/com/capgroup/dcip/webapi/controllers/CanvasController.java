package com.capgroup.dcip.webapi.controllers;

import com.capgroup.dcip.app.canvas.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * REST API for creating/updating/deleting canvases
 */
@RestController
@RequestMapping("api/dcip/canvases")
public class CanvasController {

    private CanvasService canvasService;
    private WorkbenchResourceService workbenchResourceService;

    @Autowired
    public CanvasController(CanvasService canvasService, WorkbenchResourceService workbenchResourceService) {
        this.canvasService = canvasService;
        this.workbenchResourceService = workbenchResourceService;
    }

    /**
     * returns all the canvases for a profile
     */
    @GetMapping
    public Iterable<CanvasModel> get(@RequestParam(value = "profileId", required = false) Long profileId,
                                     @RequestParam(value = "match", required = false) String match) {
        return canvasService.findAll(match, profileId);
    }

    /**
     * returns a canvas for the requested id
     */
    @GetMapping("/{id}")
    public CanvasModel get(@PathVariable("id") long id) {
        return canvasService.findCanvasById(id);
    }

    /**
     * Creates a new canvas model
     */
    @PostMapping
    public CanvasModel post(@Valid @RequestBody CanvasModel canvasModel) {
        return canvasService.createCanvas(canvasModel);
    }

    /**
     * updates an existing Canvas
     */
    @PutMapping("/{id}")
    public CanvasModel put(@PathVariable("id") long id, @Valid @RequestBody CanvasModel canvasModel) {
        return canvasService.updateCanvas(id, canvasModel);
    }

    /**
     * deletes an existing canvas
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        canvasService.deleteCanvas(id);
    }

    /**
     * Retrieves all the workbench resources for a canvas
     */
    @GetMapping("/{id}/workbench-resources")
    public List<WorkbenchResourceModel> getWorkbenchResources(@PathVariable("id") long canvasId) {
        return workbenchResourceService.findforCanvas(canvasId);
    }

    /**
     * Associates the workbench resources with the canvas. This method sets the
     * whole list, removing/adding items which aren't in the collection
     */
    @PostMapping("/{id}/workbench-resources")
    public Iterable<WorkbenchResourceModel> postWorkbenchResources(@PathVariable("id") long canvasId,
                                                                   @RequestBody Iterable<WorkbenchResourceModel> models) {
        return canvasService.setResourcesForCanvas(canvasId, models);
    }

    /**
     * Associates the workbench resources with the canvas. This method adds/updates
     * items to the collection. No removal of items
     */
    @PutMapping("/{id}/workbench-resources")
    public Iterable<WorkbenchResourceModel> putWorkbenchResources(@PathVariable("id") long canvasId,
                                                                  @RequestBody List<WorkbenchResourceModel> models) {
        return canvasService.updateResourcesForCanvas(canvasId, models);
    }

    /**
     * Removes a workbench resource from a canvas
     */
    @DeleteMapping("/{id}/workbench-resources/{workbenchResourceId}")
    public Iterable<CanvasItemModel> deleteWorkbenchResource(@PathVariable("id") long canvasId,
                                                             @PathVariable("workbenchResourceId") long workbenchResourceId) {
        return canvasService.removeWorkbenchResourceForCanvas(canvasId, workbenchResourceId);
    }

    /**
     * Find the canvas items on a canvas for a workbench resource
     */
    @GetMapping("/{id}/workbench-resources/{workbenchResourceId}/canvas-items")
    public ResponseEntity<Iterable<CanvasItemModel>> getCanvasItemsForWorkbenchResource(
            @PathVariable("id") long canvasId, @PathVariable("workbenchResourceId") long workbenchResourceId) {
        Iterable<CanvasItemModel> result = canvasService.canvasItemsForCanvasAndWorkbenchResource(canvasId,
                workbenchResourceId);

        return ResponseEntity.ok()
                .header("X-total-count", Long.toString(StreamSupport.stream(result.spliterator(), false).count()))
                .body(result);
    }
}
