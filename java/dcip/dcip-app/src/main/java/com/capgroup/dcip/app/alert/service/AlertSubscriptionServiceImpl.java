package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.alert.model.AlertSubscriptionMapper;
import com.capgroup.dcip.app.alert.model.AlertSubscriptionModel;
import com.capgroup.dcip.app.common.LinkMapper;
import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews;
import com.capgroup.dcip.domain.alert.AlertSpecification;
import com.capgroup.dcip.domain.alert.AlertSubscription;
import com.capgroup.dcip.infrastructure.repository.alert.AlertSpecificationRepository;
import com.capgroup.dcip.infrastructure.repository.alert.AlertSubscriptionRepository;
import com.capgroup.dcip.util.BatchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of AlertSubscriptionService -
 */
@Service
@EnableAllVirtualViews
public class AlertSubscriptionServiceImpl implements AlertSubscriptionService {
    AlertSubscriptionRepository alertSubscriptionRepository;
    AlertSpecificationRepository alertSpecificationRepository;
    AlertSubscriptionMapper alertSubscriptionMapper;
    EntityManager entityManager;
    LinkMapper linkMapper;

    @Autowired
    public AlertSubscriptionServiceImpl(AlertSubscriptionMapper alertSubscriptionMapper,
                                        AlertSpecificationRepository alertSpecificationRepository,
                                        EntityManager entityManager,
                                        AlertSubscriptionRepository alertSubscriptionRepository,
                                        LinkMapper linkMapper) {
        this.entityManager = entityManager;
        this.alertSubscriptionMapper = alertSubscriptionMapper;
        this.alertSpecificationRepository = alertSpecificationRepository;
        this.alertSubscriptionRepository = alertSubscriptionRepository;
        this.linkMapper = linkMapper;
    }

    @Override
    @Transactional
    public AlertSubscriptionModel subscribe(long marketDataAlertId,
                                            AlertSubscriptionModel model) {
        AlertSpecification alertSpecification =
                alertSpecificationRepository.findById(marketDataAlertId).orElseThrow(() -> new ResourceNotFoundException("AlertSpecificationEvent", Long.toString(marketDataAlertId)));

        AlertSubscription subscription = alertSubscriptionMapper.map(model);

        alertSpecification.addAlertSubscription(subscription);

        this.entityManager.flush();

        return alertSubscriptionMapper.map(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<AlertSubscriptionModel> findAllByAlertSpecificaionId(long marketDataAlertId) {
        return alertSpecificationRepository.findById(marketDataAlertId).map(AlertSpecification::getAlertSubscriptions)
                .map(alertSubscriptionMapper::mapAllAlertSubscriptions).orElse(Collections.emptyList());
    }

    @Override
    @Transactional
    public AlertSubscriptionModel update(long marketDataAlertId, long subscriptionId,
                                         AlertSubscriptionModel subscriptionModel) {
        AlertSpecification alertSpecification =
                alertSpecificationRepository.findById(marketDataAlertId).orElseThrow(() -> new ResourceNotFoundException("AlertSpecificationEvent", Long.toString(marketDataAlertId)));
        return alertSpecification.getAlertSubscriptions().stream().filter(x -> x.getId().equals(subscriptionId)).findFirst().map(s -> {
            alertSubscriptionMapper.update(subscriptionModel, s);
            return s;
        }).map(alertSubscriptionMapper::map).orElse(null);
    }

    @Override
    @Transactional
    public AlertSubscriptionModel delete(long marketDataAlertId, long subscriptionId) {
        return alertSpecificationRepository.findById(marketDataAlertId).flatMap(x -> x.removeAlertSubscription(subscriptionId))
                .map(alertSubscriptionMapper::map).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<AlertSubscriptionModel> findAll(Long profileId, Long entityId,
                                                    List<Long> ids, Long specificationId) {
        AlertSubscriptionRepository.ExpressionBuilder builder = new AlertSubscriptionRepository.ExpressionBuilder();
        return alertSubscriptionMapper.mapAllAlertSubscriptions(alertSubscriptionRepository.findAll(builder.hasProfile(profileId).and(builder.hasEntity(entityId)).and(builder.inIds(ids))
                .and(builder.hasSpecification(specificationId))));
    }

    @Override
    public Iterable<LinkModel<Long>> findAllAsLinks(Long profileId, Long entityId) {
        AlertSubscriptionRepository.ExpressionBuilder builder = new AlertSubscriptionRepository.ExpressionBuilder();
        return linkMapper.mapAll(alertSubscriptionRepository.findAll(builder.hasProfile(profileId).and(builder.hasEntity(entityId))));
    }

    @Override
    public Map<Long, List<LinkModel<Long>>> findAllAsLinks(List<Long> entityIds) {
        Stream<AlertSubscription> alertSubscriptions = BatchUtils.execute(items ->
                alertSubscriptionRepository.findAllByEntityIdInAndActiveTrue(items), entityIds);

        return alertSubscriptions.collect(Collectors.groupingBy(AlertSubscription::getEntityId,
                Collectors.mapping(linkMapper::mapEntity, Collectors.toList())));
    }

    @Override
    @Transactional(readOnly = true)
    public AlertSubscriptionModel findById(long id) {
        return alertSubscriptionMapper.map(alertSubscriptionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AlertSubscription", Long.toString(id))));
    }
}
