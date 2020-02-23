package com.capgroup.dcip.webapi.controllers;

import com.capgroup.dcip.app.thesis.model.ThesisEdgeModel;
import com.capgroup.dcip.app.thesis.model.ThesisModel;
import com.capgroup.dcip.app.thesis.model.ThesisPointModel;
import com.capgroup.dcip.app.thesis.model.ThesisTreeModel;
import com.capgroup.dcip.app.thesis.service.ThesisService;
import com.capgroup.dcip.domain.thesis.Thesis.ThesisFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("api/dcip/theses")
public class ThesisController {

    private ThesisService thesisService;

    @Autowired
    public ThesisController(ThesisService thesisService) {
        this.thesisService = thesisService;
    }

    @GetMapping("/{id}")
    public ThesisModel get(@PathVariable("id") long id) {
        return thesisService.findById(id);
    }

    /**
     * Returns the Thesis that is associated with a canvas (currently returns a
     * collection since we might add more filters in the future)
     */
    @GetMapping
    public Iterable<ThesisModel> getAll(@RequestParam("canvasId") long canvasId) {
        ThesisModel thesisModel = thesisService.findByCanvasId(canvasId);
        if (thesisModel != null) {
            return Arrays.asList(thesisModel);
        }

        return Collections.emptyList();
    }

    /**
     * Creates a Thesis
     */
    @PostMapping
    public ThesisModel post(@RequestBody ThesisModel model) {
        return thesisService.createThesis(model);
    }

    /**
     * Retrieve all the ThesisPoints for a thesis
     */
    @GetMapping("/{id}/thesis-points")
    public Iterable<ThesisPointModel> getThesisPoints(@PathVariable("id") long id) {
        return thesisService.findThesisPointsByThesis(id);
    }

    /**
     * Retrieve the full tree
     */
    @GetMapping("/{thesisId}/thesis-tree")
    public Iterable<ThesisTreeModel> getThesisTree(@PathVariable("thesisId") long thesisId) {
        return thesisService.findThesisTree(thesisId);
    }

    /**
     * Deletes a Thesis
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        thesisService.deleteThesis(id);
    }

    /**
     * Retrieve a thesis-point
     */
    @GetMapping("/{thesisId}/thesis-points/{thesisPointId}")
    public ThesisPointModel getThesisPoint(@PathVariable("thesisId") long thesisId,
                                           @PathVariable("thesisPointId") long thesisPointId) {
        return thesisService.findThesisPointByThesis(thesisId, thesisPointId);
    }

    /**
     * Adding a first level thesis point
     */
    @PostMapping("/{thesisId}/thesis-points")
    public ThesisEdgeModel postThesisPoint(@PathVariable("thesisId") long thesisId,
                                           @RequestBody ThesisPointModel thesisPoint) {
        return thesisService.createOrAddThesisPoint(thesisId, thesisPoint);
    }

    /**
     * Add a child thesis point to a parent thesis point
     */
    @PostMapping("/{thesisId}/thesis-points/{parentThesisPointId}")
    public ThesisEdgeModel postThesisPoint(@PathVariable("thesisId") long thesisId,
                                           @PathVariable("parentThesisPointId") long parentThesisPointId,
                                           @RequestBody ThesisPointModel thesisPoint) {
        return thesisService.createOrAddThesisPoint(thesisId, parentThesisPointId, thesisPoint);
    }

    /**
     * Delete a level thesis point
     */
    @DeleteMapping("/{thesisId}/thesis-points/{thesisPointId}")
    public Iterable<ThesisEdgeModel> deleteThesisPoint(@PathVariable("thesisId") long thesisId,
                                                       @PathVariable("thesisPointId") long thesisPointId) {
        return thesisService.deleteThesisPoint(thesisId, thesisPointId);
    }

    /**
     * Delete a level thesis edge
     */
    @DeleteMapping("/{thesisId}/thesis-edges/{thesisEdgeId}")
    public Iterable<ThesisEdgeModel> deleteThesisEdge(@PathVariable("thesisId") long thesisId,
                                                      @PathVariable("thesisEdgeId") long thesisEdgeId) {
        return thesisService.deleteThesisEdge(thesisId, thesisEdgeId);
    }

    @PutMapping("/{thesisId}/thesis-points/{thesisPointId}")
    public ThesisPointModel putThesisPoint(@PathVariable("thesisId") long thesisId,
                                           @PathVariable("thesisPointId") long thesisPointId,
                                           @RequestBody ThesisPointModel thesisPoint) {
        return thesisService.updateThesisPoint(thesisId, thesisPointId, thesisPoint);
    }

    @GetMapping("/{thesisId}/thesis-edges")
    public Iterable<ThesisEdgeModel> getThesisEdges(@PathVariable("thesisId") long thesisId,
                                                    @RequestParam(name = "thesisPointId", required = false) Long thesisPointId,
                                                    @RequestParam(name = "filter", required = false) List<ThesisFilter> filters) {
        return thesisService.findThesisEdges(thesisId, thesisPointId,
                (filters == null || filters.isEmpty()) ? EnumSet.allOf(ThesisFilter.class) : EnumSet.copyOf(filters));
    }

    /**
     * Deletes a thesis edge given parent/child thesis points
     */
    @DeleteMapping("/{thesisId}/thesis-edges")
    public Iterable<ThesisEdgeModel> deleteThesisEdges(@PathVariable("thesisId") long thesisId,
                                                       @RequestParam("childThesisPointId") long childThesisPointId,
                                                       @RequestParam(value = "parentThesisPointId", required = false) Long parentThesisPointId) {
        return thesisService.deleteThesisEdge(thesisId, childThesisPointId, parentThesisPointId);
    }

    @GetMapping("/{thesisId}/thesis-edges/{thesisEdgeId}")
    public ThesisEdgeModel getThesisEdge(@PathVariable("thesisId") long thesisId,
                                         @PathVariable("thesisEdgeId") long thesisEdgeId) {
        return thesisService.findThesisEdge(thesisId, thesisEdgeId);
    }
}
