package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.alert.model.AlertSpecificationEventModel;
import com.capgroup.dcip.app.alert.model.AlertSpecificationEventMapper;
import com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews;
import com.capgroup.dcip.domain.alert.AlertSpecification;
import com.capgroup.dcip.domain.alert.AlertSpecificationEvent;
import com.capgroup.dcip.domain.alert.AlertSubscription;
import com.capgroup.dcip.domain.alert.AlertSubscriptionEvent;
import com.capgroup.dcip.infrastructure.repository.alert.AlertSpecificationEventRepository;
import com.capgroup.dcip.infrastructure.repository.alert.AlertSpecificationRepository;
import com.capgroup.dcip.infrastructure.repository.alert.AlertSubscriptionEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class AlertSpecificationEventServiceImpl implements AlertSpecificationEventService {
    private AlertSpecificationEventRepository alertSpecificationEventRepository;
    private AlertSpecificationEventMapper alertSpecificationEventMapper;
    private AlertSpecificationRepository alertSpecificationRepository;
    private AlertSubscriptionEventRepository alertSubscriptionEventRepository;
    private EntityManager entityManager;

    @Autowired
    public AlertSpecificationEventServiceImpl(AlertSpecificationRepository alertSpecificationRepository,
                                              AlertSpecificationEventRepository repository,
                                              AlertSpecificationEventMapper mapper,
                                              AlertSubscriptionEventRepository alertSubscriptionEventRepository,
                                              EntityManager entityManager) {
        this.alertSpecificationEventRepository = repository;
        this.alertSpecificationEventMapper = mapper;
        this.entityManager = entityManager;
        this.alertSpecificationRepository = alertSpecificationRepository;
        this.alertSubscriptionEventRepository = alertSubscriptionEventRepository;
    }

    boolean isDuplicate(AlertSpecificationEvent event){
        if (alertSpecificationEventRepository.findAllByEvaluationDateAndAlertSpecification_Id(event.getEvaluationDate(),
                event.getAlertSpecification().getId()).stream().anyMatch(x -> x.isDuplicate(event))) {
            log.warn("Filtering Duplicate Event for AlertSpecification:{} and EvaluationDate:{}",
                    event.getAlertSpecification().getId(), event.getEvaluationDate());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Iterable<AlertSpecificationEventModel> create(long alertSpecificationId,
                                                         Iterable<AlertSpecificationEventModel> models) {
        Stream<AlertSpecificationEvent> alertSpecificationEvents =
                alertSpecificationEventMapper.mapAllModels(alertSpecificationId, models);

        // filter duplicates
        alertSpecificationEvents = alertSpecificationEvents.filter(x->!isDuplicate(x));

        AlertSpecification alertSpecification =
                alertSpecificationRepository.findById(alertSpecificationId).orElseThrow(() ->
                        new ResourceNotFoundException("AlertSpecification", Long.toString(alertSpecificationId)));

        // save the alert specification events
        Iterable<AlertSpecificationEvent> alertSpecificationEventResults =
                alertSpecificationEventRepository.saveAll(alertSpecificationEvents.collect(Collectors.toList()));

        // create the alert subscription events
        // Only create the subscription event if the date of the subscription is <= to the evaluation date
        // and the subscription is active
        List<AlertSubscriptionEvent> alertSubscriptionEvents =
                alertSpecification.getAlertSubscriptions().stream().filter(AlertSubscription::isActive).flatMap(alertSubscription ->
                        StreamSupport.stream(alertSpecificationEventResults.spliterator(), false)
                                .filter(event -> LocalDateTime.of(alertSubscription.getActiveTimestampChange().toLocalDate(),
                                        LocalTime.MIDNIGHT).compareTo(
                                        LocalDateTime.of(event.getEvaluationDate().toLocalDate(), LocalTime.MIDNIGHT)) <= 0)
                                .map(event -> alertSubscription.createEvent(event))).collect(Collectors.toList());


        Iterable<AlertSubscriptionEvent> results = alertSubscriptionEventRepository.saveAll(alertSubscriptionEvents);

        entityManager.flush();

        return alertSpecificationEventMapper.mapAllEvents(alertSpecificationEventResults);
    }
}
