package com.capgroup.dcip.webapi.controllers.alert;

import com.capgroup.dcip.app.alert.model.AlertSubscriptionEventModel;
import com.capgroup.dcip.app.alert.service.AlertSubscriptionEventService;
import com.capgroup.dcip.domain.alert.AlertSubscriptionEventStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller for external API for Events
 */
@RestController
@RequestMapping("api/dcip/alert/subscription-events")
public class AlertSubscriptionEventController {

    AlertSubscriptionEventService alertSubscriptionEventService;

    @Autowired
    public AlertSubscriptionEventController(AlertSubscriptionEventService alertSubscriptionEventService) {
        this.alertSubscriptionEventService = alertSubscriptionEventService;
    }

    @GetMapping
    public Iterable<AlertSubscriptionEventModel> get(@RequestParam(name = "profileId", required = false) Long profileId,
                                                     @RequestParam(name = "entityId", required = false) List<Long> entityIds,
                                                     @RequestParam(name = "id", required = false) List<UUID> ids,
                                                     @RequestParam(name = "evaluationResult", required = false) Boolean evaluationResults,
                                                     @RequestParam(name = "createdSince", required = false) @DateTimeFormat(iso =
                                                             DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdSince,
                                                     @RequestParam(name = "createdTill", required = false) @DateTimeFormat(iso =
                                                             DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTill,
                                                     @RequestParam(name = "evaluationSince", required = false) @DateTimeFormat(iso =
                                                             DateTimeFormat.ISO.DATE_TIME) LocalDateTime evaluationSince,
                                                     @RequestParam(name = "evaluationTill", required = false) @DateTimeFormat(iso =
                                                             DateTimeFormat.ISO.DATE_TIME) LocalDateTime evaluationTill,
                                                     @RequestParam(name = "includeErrors", required = false,
                                                             defaultValue = "false") Boolean includeErrors) {
        return alertSubscriptionEventService.findAll(profileId, entityIds, ids, evaluationResults, evaluationSince,
                evaluationTill, createdSince, createdTill, includeErrors);
    }

    @GetMapping("/{id}")
    public AlertSubscriptionEventModel get(@PathVariable("id") UUID id) {
        return alertSubscriptionEventService.findById(id);
    }

    @PostMapping("/{id}/{status}")
    public AlertSubscriptionEventStatus.Status postStatus(@PathVariable("id") UUID id,
                                                          @PathVariable("status") AlertSubscriptionEventStatus.Status status) {
        return alertSubscriptionEventService.changeStatus(id, status);
    }
}
