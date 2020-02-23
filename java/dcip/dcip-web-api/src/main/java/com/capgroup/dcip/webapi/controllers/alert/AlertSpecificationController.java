package com.capgroup.dcip.webapi.controllers.alert;

import com.capgroup.dcip.app.alert.model.AlertSpecificationCreationModel;
import com.capgroup.dcip.app.alert.model.AlertSpecificationEventModel;
import com.capgroup.dcip.app.alert.model.AlertSpecificationModel;
import com.capgroup.dcip.app.alert.model.AlertSubscriptionModel;
import com.capgroup.dcip.app.alert.service.AlertSpecificationEventService;
import com.capgroup.dcip.app.alert.service.AlertSpecificationService;
import com.capgroup.dcip.app.alert.service.AlertSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * External contract for
 */
@RestController
@RequestMapping("api/dcip/alert/specifications")
public class AlertSpecificationController {

    private AlertSpecificationService alertSpecificationService;
    private AlertSubscriptionService alertSubscriptionService;
    private AlertSpecificationEventService alertSpecificationEventService;

    @Autowired
    public AlertSpecificationController(AlertSpecificationService alertSpecificationService,
                                        AlertSubscriptionService alertSubscriptionService,
                                        AlertSpecificationEventService alertSpecificationEventService) {
        this.alertSpecificationService = alertSpecificationService;
        this.alertSubscriptionService = alertSubscriptionService;
        this.alertSpecificationEventService = alertSpecificationEventService;
    }

    @GetMapping
    public Iterable<AlertSpecificationModel> getAlertSpecifications(@RequestParam(name = "active", required = false) Boolean active,
                                                                    @RequestParam(name = "id",
                                                                            required = false) List<Long> alertSpecificationIds,
                                                                    @RequestParam(name="targetDateLimit", required=false) LocalDate targetDate) {
        return alertSpecificationService.findAll(active, alertSpecificationIds, targetDate);
    }

    @GetMapping("/{alertSpecificationId}")
    public AlertSpecificationModel getAlertSpecificationById(@PathVariable("alertSpecificationId") long id) {
        return alertSpecificationService.findById(id);
    }

    @PostMapping
    public AlertSpecificationCreationModel postAlertSpecification(@RequestBody AlertSpecificationCreationModel alert) {
        return alertSpecificationService.create(alert);
    }

    @PutMapping("/{alertSpecificationId}")
    public AlertSpecificationModel putAlertSpecification(@PathVariable("alertSpecificationId") long alertSpecificationId,
                                                         @RequestBody AlertSpecificationModel alert) {
        return alertSpecificationService.update(alertSpecificationId, alert);
    }

    @DeleteMapping("/{alertSpecificationId}")
    public AlertSpecificationModel deleteAlertSpecification(@PathVariable("alertSpecificationId") long alertSpecificationId) {
        return alertSpecificationService.delete(alertSpecificationId);
    }

    @GetMapping("/{alertSpecificationId}/subscriptions")
    public Iterable<AlertSubscriptionModel> getSubscription(@PathVariable("alertSpecificationId") long id) {
        return alertSubscriptionService.findAllByAlertSpecificaionId(id);
    }

    @PostMapping("/{alertSpecificationId}/subscriptions")
    public AlertSubscriptionModel postSubscription(
            @PathVariable("alertSpecificationId") long marketDataId,
            @RequestBody AlertSubscriptionModel subscription) {
        return alertSubscriptionService.subscribe(marketDataId, subscription);
    }

    @PostMapping("/{alertSpecificationId}/events")
    public Iterable<AlertSpecificationEventModel> postSpecificationEvent(@PathVariable("alertSpecificationId") long alertSpecificationId, @RequestBody
            List<AlertSpecificationEventModel> events) {
        return alertSpecificationEventService.create(alertSpecificationId, events);
    }

    @PutMapping("/{alertSpecificationId}/subscriptions/{subscriptionId}")
    public AlertSubscriptionModel putSubscription(@PathVariable("alertSpecificationId") long marketDataId,
                                                  @PathVariable("subscriptionId") long subscriptionId,
                                                  @RequestBody AlertSubscriptionModel subscription) {
        return alertSubscriptionService.update(marketDataId, subscriptionId, subscription);
    }

    @DeleteMapping("/{alertSpecificationId}/subscriptions/{subscriptionId}")
    public AlertSubscriptionModel deleteSubscription(@PathVariable("alertSpecificationId") long marketDataId,
                                                     @PathVariable("subscriptionId") long subscriptionId) {
        return alertSubscriptionService.delete(marketDataId, subscriptionId);
    }

 /*   @GetMapping("/{alertSpecificationId}/events?subscriptionId={subscriptionId}")
    public Iterable<MarketDataAlertEventModel> getEvents(@PathVariable("alertSpecificationId") long id,
                                                         @PathVariable("subscriptionId") long subscriptionId) {
        return null;
    }

    @GetMapping("/{alertSpecificationId}/events/{subscriptionId}/actions")
    public List<MarketDataAlertEventActionModel> getActions(@PathVariable("alertSpecificationId") long id,
                                                            @PathVariable("subscriptionId") long subscriptionId) {
        return null;
    }*/
}
