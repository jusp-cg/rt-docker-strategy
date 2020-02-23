package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.alert.model.AlertSubscriptionEventActionModel;
import com.capgroup.dcip.app.alert.model.AlertSubscriptionEventMapper;
import com.capgroup.dcip.app.alert.model.AlertSubscriptionEventModel;
import com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews;
import com.capgroup.dcip.domain.alert.AlertActionType;
import com.capgroup.dcip.domain.alert.AlertSubscriptionEvent;
import com.capgroup.dcip.domain.alert.AlertSubscriptionEventAction;
import com.capgroup.dcip.domain.alert.AlertSubscriptionEventStatus;
import com.capgroup.dcip.infrastructure.repository.alert.AlertActionTypeRepository;
import com.capgroup.dcip.infrastructure.repository.alert.AlertSubscriptionEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for creating and querying AlertSubscriptionEvents
 */
@Service
@EnableAllVirtualViews
public class AlertSubscriptionEventServiceImpl implements AlertSubscriptionEventService {
    private AlertSubscriptionEventRepository alertSubscriptionEventRepository;
    private AlertActionTypeRepository alertActionTypeRepository;
    private AlertSubscriptionEventMapper alertSubscriptionEventMapper;
    private EntityManager entityManager;

    @Autowired
    public AlertSubscriptionEventServiceImpl(AlertSubscriptionEventRepository alertSubscriptionEventRepository,
                                             AlertSubscriptionEventMapper alertEventMapper,
                                             EntityManager entityManager,
                                             AlertActionTypeRepository alertTypeRepository) {
        this.alertSubscriptionEventRepository = alertSubscriptionEventRepository;
        this.alertSubscriptionEventMapper = alertEventMapper;
        this.entityManager = entityManager;
        this.alertActionTypeRepository = alertTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<AlertSubscriptionEventModel> findAll(Long profileId, List<Long> entityIds,
                                                         List<UUID> ids,
                                                         Boolean evaluationResult,
                                                         LocalDateTime evaluationSince,
                                                         LocalDateTime evaluationTill,
                                                         LocalDateTime createdSince,
                                                         LocalDateTime createdTill,
                                                         Boolean includeErrors) {
        return alertSubscriptionEventMapper.mapAll(alertSubscriptionEventRepository.findAllBy(entityIds == null || entityIds.isEmpty() ? null : entityIds, profileId,
                ids == null || ids.isEmpty() ? null : ids, evaluationResult, includeErrors, createdSince, createdTill
                , evaluationSince, evaluationTill));
    }

    @Override
    @Transactional(readOnly = true)
    public AlertSubscriptionEventModel findById(UUID id) {
        return alertSubscriptionEventMapper.map(alertSubscriptionEventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AlertSubscriptionEvent", id.toString())));
    }

    @Override
    @Transactional
    public AlertSubscriptionEventStatus.Status changeStatus(UUID id, AlertSubscriptionEventStatus.Status status) {
        // get the event
        AlertSubscriptionEvent event = alertSubscriptionEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AlertSubscriptionEvent", id.toString()));

        if (event.getAlertSubscriptionEventStatus() == null) {
            event.setAlertSubscriptionEventStatus(new AlertSubscriptionEventStatus(status));
        } else if (event.getAlertSubscriptionEventStatus().getEventStatus().compareTo(status) < 0) {
            event.getAlertSubscriptionEventStatus().setEventStatus(status);
        }

        return event.getAlertSubscriptionEventStatus().getEventStatus();
    }

    @Override
    @Transactional
    public AlertSubscriptionEventActionModel createAction(UUID alertId, long entityId, long actionTypeId) {
        // get the event
        AlertSubscriptionEvent event = alertSubscriptionEventRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("AlertSubscriptionEvent", alertId.toString()));

        // get the alert action type
        AlertActionType alertActionType = alertActionTypeRepository.findById(actionTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("AlertActionType", Long.toString(actionTypeId)));

        // create the action
        AlertSubscriptionEventAction action = new AlertSubscriptionEventAction(alertActionType, entityId);

        // mapEntity the action to a model
        event.addAlertSubscriptionAction(action);

        return alertSubscriptionEventMapper.map(action);
    }
}
