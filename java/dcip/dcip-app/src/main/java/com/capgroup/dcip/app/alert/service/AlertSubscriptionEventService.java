package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.app.alert.model.AlertSubscriptionEventActionModel;
import com.capgroup.dcip.app.alert.model.AlertSubscriptionEventModel;
import com.capgroup.dcip.domain.alert.AlertSubscriptionEventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for Creating/updating/querying alert subscription events
 */
public interface AlertSubscriptionEventService {
    Iterable<AlertSubscriptionEventModel> findAll(Long profileId, List<Long> entityIds,
                                                  List<UUID> ids,
                                                  Boolean evaluationResult,
                                                  LocalDateTime evaluatedSince,
                                                  LocalDateTime evaluatedTill,
                                                  LocalDateTime createdSince,
                                                  LocalDateTime createdTill,
                                                  Boolean includeErrors);

    AlertSubscriptionEventModel findById(UUID id);

    AlertSubscriptionEventStatus.Status changeStatus(UUID id, AlertSubscriptionEventStatus.Status status);

    AlertSubscriptionEventActionModel createAction(UUID alertId, long entityId, long actionTypeId);
}
