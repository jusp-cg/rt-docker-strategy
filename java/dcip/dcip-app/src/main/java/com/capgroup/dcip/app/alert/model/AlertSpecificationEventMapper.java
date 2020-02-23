package com.capgroup.dcip.app.alert.model;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.alert.service.AlertSpecificationEventMessageBuilder;
import com.capgroup.dcip.app.common.LinkMapper;
import com.capgroup.dcip.app.common.ToEntity;
import com.capgroup.dcip.app.common.ToLongLink;
import com.capgroup.dcip.app.common.UtilityLinkMapper;
import com.capgroup.dcip.domain.alert.AlertSpecification;
import com.capgroup.dcip.domain.alert.AlertSpecificationEvent;
import com.capgroup.dcip.infrastructure.repository.alert.AlertSpecificationRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

/**
 * Mapping between an AlertSpecificationEvent and its corresponding model
 */
@Mapper(uses = {LinkMapper.class, AlertCriteriaEvaluationMapper.class})
public abstract class AlertSpecificationEventMapper {
    @Autowired
    AlertSpecificationEventMessageBuilder eventMessageBuilder;

    @Autowired
    public AlertSpecificationRepository alertSpecificationRepository;

    @Mapping(target = "alertSpecification", qualifiedBy = {UtilityLinkMapper.class, ToEntity.class})
    public abstract AlertSpecificationEvent map(AlertSpecificationEventModel model);

    public abstract Iterable<AlertSpecificationEventModel> mapAllEvents(Iterable<AlertSpecificationEvent> model);

    public Stream<AlertSpecificationEvent> mapAllModels(long alertSpecificationId,
                                                      Iterable<AlertSpecificationEventModel> models) {
        AlertSpecification alertSpecification =
                alertSpecificationRepository.findById(alertSpecificationId).orElseThrow(() -> new ResourceNotFoundException("AlertSpecification", Long.toString(alertSpecificationId)));

        return mapAllModels(models).map(x -> {
            x.setAlertSpecification(alertSpecification);
            return x;
        });
    }

    protected abstract Stream<AlertSpecificationEvent> mapAllModels(Iterable<AlertSpecificationEventModel> model);

    @Mapping(target = "alertSpecification", qualifiedBy = {UtilityLinkMapper.class, ToLongLink.class})
    public abstract AlertSpecificationEventModel map(AlertSpecificationEvent model);

    @AfterMapping
    protected void afterMapping(AlertSpecificationEventModel model, @MappingTarget AlertSpecificationEvent event){
        event.setMessage(eventMessageBuilder.build(event));
    }
}
