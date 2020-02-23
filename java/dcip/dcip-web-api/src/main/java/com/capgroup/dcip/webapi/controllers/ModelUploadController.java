package com.capgroup.dcip.webapi.controllers;

import com.capgroup.dcip.app.models.ModelDetails;
import com.capgroup.dcip.app.models.ModelService;
import com.capgroup.dcip.app.models.VersionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * External API for uploading/retrieving models
 */
@RestController
@Slf4j
@RequestMapping("api/dcip/tool/models")
public class ModelUploadController {
    private ModelService modelService;

    @Autowired
    public ModelUploadController(ModelService service) {
        this.modelService = service;
    }

    //upload new model
    @PostMapping
    public ResponseEntity<ModelDetails> post(@RequestBody ModelDetails details) {
        return new ResponseEntity<>(modelService.createModel(details), HttpStatus.OK);
    }

    //update existing model in database
    @PutMapping("/{modelId}")
    public ResponseEntity<ModelDetails> put(@PathVariable("modelId") Long modelId, @RequestBody ModelDetails details) {
        return new ResponseEntity<>(modelService.updateModel(modelId, details), HttpStatus.OK);
    }

    //mark model deleted in database
    @DeleteMapping("/{modelId}")
    public ResponseEntity<ModelDetails> delete(@PathVariable("modelId") long modelId) {
        return new ResponseEntity<>(modelService.deleteModel(modelId), HttpStatus.OK);
    }

    /**
     * Get the models for a canvas
     */
    @GetMapping
    public Iterable<ModelDetails> get(@RequestParam("canvasId") Long canvasId) {
        return modelService.findModels(canvasId);
    }

    //get list of version metadata corresponding to a given model id
    @GetMapping("/{modelId}/versions")
    public ResponseEntity<Iterable<VersionResponse>> getVersionMetadata(@PathVariable("modelId") long modelId) {
        return new ResponseEntity<>(modelService.getVersionMetadata(modelId), HttpStatus.OK);
    }

    //upload new file for a given model
    @PostMapping("/{modelId}/versions")
    public ResponseEntity<VersionResponse> postVersion(@PathVariable("modelId") long modelId,
                                                       @RequestParam("file") MultipartFile file, @RequestParam(
            "comments") String comments) throws IOException {
        return new ResponseEntity<>(modelService.uploadVersion(modelId, file, comments), HttpStatus.OK);
    }

    //get version content corresponding to model
    @GetMapping("/{modelId}/versions/{versionId}")
    public ResponseEntity<Resource> getVersionContent(@PathVariable("modelId") long modelId, @PathVariable("versionId"
    ) String versionId) throws IOException {
        return new ResponseEntity<>(modelService.downloadVersion(modelId, versionId), HttpStatus.OK);
    }

    //delete version accordingly
    @DeleteMapping("/{modelId}/versions/{versionId}")
    public VersionResponse deleteVersion(@PathVariable("modelId") long modelId,
                                         @PathVariable("versionId") String versionId) {
        return modelService.deleteVersion(modelId, versionId);
    }
}
