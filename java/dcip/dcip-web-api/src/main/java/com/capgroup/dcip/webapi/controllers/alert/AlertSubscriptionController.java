package com.capgroup.dcip.webapi.controllers.alert;

import com.capgroup.dcip.app.alert.model.AlertSubscriptionModel;
import com.capgroup.dcip.app.alert.service.AlertSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * External REST API for operations on AlertSubscriptions
 */
@RestController
@RequestMapping("api/dcip/alert/subscriptions")
public class AlertSubscriptionController {

    private AlertSubscriptionService alertSubscriptionService;

    @Autowired
    public AlertSubscriptionController(AlertSubscriptionService service) {
        this.alertSubscriptionService = service;
    }

    @GetMapping
    public Iterable<AlertSubscriptionModel> get(@RequestParam(name = "profileId", required = false) Long profileId,
                                                @RequestParam(name = "entityId", required = false) Long entityId,
                                                @RequestParam(name = "id", required = false) List<Long> alertSubscriptionIds,
                                                @RequestParam(name = "specificationId", required = false) Long specificationId) {
        return alertSubscriptionService.findAll(profileId, entityId, alertSubscriptionIds,
                specificationId);
    }

    @GetMapping("/{id}")
    public AlertSubscriptionModel get(@PathVariable("id") long id) {
        return alertSubscriptionService.findById(id);
    }
}
