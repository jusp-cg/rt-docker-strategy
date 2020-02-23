package com.capgroup.dcip.webapi.controllers;

import com.capgroup.dcip.app.canvas.CanvasService;
import com.capgroup.dcip.app.relationship.RelationshipModel;
import com.capgroup.dcip.app.relationship.RelationshipService;
import com.capgroup.dcip.domain.canvas.WorkbenchResource.WorkbenchResourceId;
import com.capgroup.dcip.domain.relationship.RelationshipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/dcip/relation")
public class RelationshipController {

    private RelationshipService relationshipService;
    private CanvasService canvasService;

    @Autowired
    public RelationshipController(RelationshipService relationshipService, CanvasService canvasService) {
        this.relationshipService = relationshipService;
        this.canvasService = canvasService;
    }

    @GetMapping
    public Iterable<RelationshipModel> getAll(@RequestParam("consumerId") Long consumerId) {
        return relationshipService.findRelationsByConsumerId(consumerId);
    }

    @PostMapping("/canvas/charts")
    public RelationshipModel linkCanvasWithCharts(@RequestBody RelationshipModel relationshipModel) {
        RelationshipType relationshipType = relationshipService.findRelationshipTypeByRoleType(1L, 2L);
        relationshipModel.setRelationshipTypeId(relationshipType.getId());
        RelationshipModel result = relationshipService.createRelation(relationshipModel);

        canvasService.addCanvasItem(result.getFirstEntityId(), WorkbenchResourceId.CHARTS, result.getSecondEntityId());

        return result;
    }

    @PostMapping("/thesispoint/charts")
    public RelationshipModel linkThesispointWithCharts(@RequestBody RelationshipModel relationshipModel) {
        RelationshipType relationshipType = relationshipService.findRelationshipTypeByRoleType(3L, 2L);
        relationshipModel.setRelationshipTypeId(relationshipType.getId());

        return relationshipService.createRelation(relationshipModel);
    }

    @PostMapping("/thesispoint/notes")
    public RelationshipModel linkThesispointWithNode(@RequestBody RelationshipModel relationshipModel) {
        RelationshipType relationshipType = relationshipService.findRelationshipTypeByRoleType(3L, 4L);
        relationshipModel.setRelationshipTypeId(relationshipType.getId());

        return relationshipService.createRelation(relationshipModel);
    }

    @DeleteMapping
    public void removeLink(@RequestBody RelationshipModel relationshipModel) {
        relationshipService.removeRelation(relationshipModel);
    }
}
