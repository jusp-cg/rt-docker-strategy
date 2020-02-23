package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.alert.model.AlertSpecificationCreationModel;
import com.capgroup.dcip.app.alert.model.AlertSpecificationMapper;
import com.capgroup.dcip.app.alert.model.AlertSpecificationModel;
import com.capgroup.dcip.app.alert.model.AlertSubscriptionModel;
import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews;
import com.capgroup.dcip.domain.alert.AlertSpecification;
import com.capgroup.dcip.infrastructure.repository.alert.AlertSpecificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AlertSpecificationService interface - see interface for definitions
 */
@Service
@EnableAllVirtualViews
public class AlertSpecificationServiceImpl implements AlertSpecificationService {

    AlertSpecificationRepository alertSpecificationRepository;
    AlertSpecificationMapper alertSpecificationMapper;
    EntityManager entityManager;
    AlertSubscriptionService alertSubscriptionService;
    RequestContextService requestContextService;

    @Autowired
    public AlertSpecificationServiceImpl(AlertSpecificationRepository alertSpecificationRepository,
                                         AlertSpecificationMapper alertSpecificationMapper,
                                         AlertSubscriptionService alertSubscriptionService,
                                         RequestContextService requestContextService,
                                         EntityManager entityManager) {
        this.alertSpecificationRepository = alertSpecificationRepository;
        this.alertSpecificationMapper = alertSpecificationMapper;
        this.entityManager = entityManager;
        this.alertSubscriptionService = alertSubscriptionService;
        this.requestContextService = requestContextService;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<AlertSpecificationModel> findAll(Boolean active,
                                                     List<Long> alertSpecificationIds,
                                                     LocalDate targetDate) {
        Iterable<AlertSpecificationModel> result =
                alertSpecificationMapper.mapAllAlertSpecifications(alertSpecificationRepository.findAllBy(
                alertSpecificationIds == null || alertSpecificationIds.isEmpty() ? null : alertSpecificationIds,
                        active, targetDate));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public AlertSpecificationModel findById(long id) {
        return alertSpecificationRepository.findById(id).map(alertSpecificationMapper::map).orElseThrow(() -> new ResourceNotFoundException("AlertSpecificationEvent", Long.toString(id)));
    }

    @Override
    @Transactional
    public AlertSpecificationModel create(AlertSpecificationModel model) {
        AlertSpecification alert = this.alertSpecificationMapper.map(model);
        alert = alertSpecificationRepository.save(alert);
        entityManager.flush();
        return alertSpecificationMapper.map(alert);
    }

    @Override
    @Transactional
    public AlertSpecificationCreationModel create(AlertSpecificationCreationModel model) {
        AlertSpecificationModel alertSpecification = create(model.getSpecification());
        List<AlertSubscriptionModel> subscriptions = model.getSubscriptions() == null ? Collections.emptyList() :
                model.getSubscriptions().stream().map(x ->
                        alertSubscriptionService.subscribe(alertSpecification.getId(),
                                x)).collect(Collectors.toList());
        return new AlertSpecificationCreationModel(alertSpecification, subscriptions);
    }

    @Override
    @Transactional
    public AlertSpecificationModel update(long id, AlertSpecificationModel model) {
        AlertSpecification alertSpecification =
                alertSpecificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                        "AlertSpecificationEvent", Long.toString(id)));

        // vslidate the name is unique for the profile if it has changed
        if (!model.getName().equals(alertSpecification.getName()) && alertSpecificationRepository.existsByEventProfileAndNameIgnoreCaseAndActiveIsTrue(requestContextService.currentProfile(),
                model.getName())) {
            throw new IllegalArgumentException("Duplicate alert name:" + model.getName());
        }


        this.alertSpecificationMapper.update(model, alertSpecification);
        alertSpecification = alertSpecificationRepository.save(alertSpecification);
        entityManager.flush();
        return alertSpecificationMapper.map(alertSpecification);
    }

    @Override
    @Transactional
    public AlertSpecificationModel delete(long id) {
        return alertSpecificationRepository.findById(id).map(x -> {
            alertSpecificationRepository.deleteById(id);
            return alertSpecificationMapper.map(x);
        }).orElse(null);
    }
}
