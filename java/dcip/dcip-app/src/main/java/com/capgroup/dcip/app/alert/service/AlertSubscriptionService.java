package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.app.alert.model.AlertSubscriptionModel;
import com.capgroup.dcip.app.common.LinkModel;

import java.util.List;
import java.util.Map;

/**
 * Defines the application level operations for an AlertSubscription
 */
public interface AlertSubscriptionService {
    AlertSubscriptionModel subscribe(long marketDataAlertId, AlertSubscriptionModel subscription);

    Iterable<AlertSubscriptionModel> findAllByAlertSpecificaionId(long marketDataAlertId);

    AlertSubscriptionModel update(long marketDataAlertId, long subscriptionId,
                                  AlertSubscriptionModel subscription);

    AlertSubscriptionModel delete(long marketDataAlertId, long subscriptionId);

    Iterable<AlertSubscriptionModel> findAll(Long profileId, Long entityId, List<Long> ids,
                                             Long specificationId);

    /**
     * find all links that reference the entity
     */
    Iterable<LinkModel<Long>> findAllAsLinks(Long profileId, Long entityId);

    /**
     * find all links that reference the entityIds
     */
    Map<Long, List<LinkModel<Long>>> findAllAsLinks(List<Long> entityIds);

    AlertSubscriptionModel findById(long id);
}
