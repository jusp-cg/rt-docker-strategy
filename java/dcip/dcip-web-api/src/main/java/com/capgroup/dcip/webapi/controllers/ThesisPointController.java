package com.capgroup.dcip.webapi.controllers;

import com.capgroup.dcip.app.thesis.model.ThesisPointModel;
import com.capgroup.dcip.app.thesis.service.ThesisService;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("api/dcip/thesis-points")
public class ThesisPointController {

    private ThesisService thesisService;

    @Autowired
    public ThesisPointController(ThesisService thesisService) {
        this.thesisService = thesisService;
    }

    @GetMapping
    public Iterable<ThesisPointModel> get(@RequestParam(name = "profileId", required = false) Long profileId,
                                          @RequestParam(name = "status", required = false) List<TemporalEntity.Status> entityStatus) {
        return thesisService.findAllThesisPoints(profileId, entityStatus == null || entityStatus.isEmpty() ?
                EnumSet.allOf(TemporalEntity.Status.class)
                : EnumSet.copyOf(entityStatus));
    }

    @GetMapping("/{id}")
    public ThesisPointModel get(@PathVariable("id") long id) {
        return thesisService.findThesisPoint(id);
    }

    @GetMapping("/{id}/versions")
    public Iterable<ThesisPointModel> getVersions(@PathVariable("id") long id){
        return thesisService.findVersionsOfThesisPoint(id);
    }
}
