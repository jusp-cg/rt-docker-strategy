package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.app.event.application.DeletedApplicationEvent;
import com.capgroup.dcip.app.thesis.model.ThesisEdgeModel;
import com.capgroup.dcip.app.thesis.service.ThesisService;
import com.capgroup.dcip.domain.alert.AlertSubscription;
import com.capgroup.dcip.domain.thesis.Thesis;
import com.capgroup.dcip.infrastructure.repository.alert.AlertSubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Processes Thesis events that affects Alerts
 */
@Component
@Slf4j
public class AlertThesisEventsHandler {

    AlertSubscriptionRepository alertSubscriptionRepository;
    ThesisService thesisService;

    @Autowired
    public AlertThesisEventsHandler(AlertSubscriptionRepository alertSubscriptionRepository,
                                    ThesisService thesisService) {
        this.alertSubscriptionRepository = alertSubscriptionRepository;
        this.thesisService = thesisService;
    }

    /**
     * If the 'Child' point associated with the thesis edge is not referenced anywhere else
     * for a user, and that user has subscriptions - set those subscriptions to be inactive
     */
    @EventListener
    public void onThesisEdgeDelete(DeletedApplicationEvent<ThesisEdgeModel> thesisEdgeEvent) {
        if (log.isDebugEnabled()) {
            log.debug("Processing thesis edge delete for alerts, edge id:{}, point id:{}",
                    thesisEdgeEvent.getTarget().getId(),
                    thesisEdgeEvent.getTarget().getChildThesisPoint().getId());
        }

        // get the alert subscriptions that have a reference to the 'child' thesis point of the edge
        Set<AlertSubscription> subscriptions =
                alertSubscriptionRepository.findAllByEntityIdAndActiveTrue(thesisEdgeEvent.getTarget().getChildThesisPoint().getId());


        // no subscriptions of the thesis point - ignore it
        if (subscriptions.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("No subscriptions found for thesis point:{}",
                        thesisEdgeEvent.getTarget().getChildThesisPoint().getId());
            }
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Found subscriptions:{} that reference the thesis point", String.join(",",
                    subscriptions.stream().map(x -> x.getId().toString()).collect(Collectors.toList())));
        }

        // reject the delete
        thesisEdgeEvent.reject();

        // Find all thesis edges that reference this thesis point (no matter which thesis)
        // filter by the event thesis point
        List<ThesisEdgeModel> thesisEdges = StreamSupport.stream(thesisService.findThesisEdges(null,
                thesisEdgeEvent.getTarget().getChildThesisPoint().getId(),
                EnumSet.of(Thesis.ThesisFilter.CHILD)).spliterator(), false).filter(x -> !x.getId().equals(thesisEdgeEvent.getTarget().getId())).collect(Collectors.toList());

        if (log.isDebugEnabled()) {
            log.debug("Edges that reference the thesis point:", String.join("",
                    thesisEdges.stream().map(x -> x.getId().toString()).collect(Collectors.toList())));
        }

        // if there is still a thesis edge that has the same user as the subscription - don't inactivate the
        // subscription
        subscriptions.stream().filter(subscription ->
                !thesisEdges.stream().anyMatch(x -> x.getProfileId() == subscription.getEvent().getProfile().getId())
        ).forEach(subscription -> {
            if (log.isDebugEnabled()) {
                log.debug("Inactivating subscription:{}", subscription.getId());
            }
            subscription.setActive(false);
            // inactivate the specification
            if (subscription.getAlertSpecification().getAlertSubscriptions().stream().allMatch(x -> !x.isActive())) {
                subscription.getAlertSpecification().setActive(false);
            }
        });
    }
}
