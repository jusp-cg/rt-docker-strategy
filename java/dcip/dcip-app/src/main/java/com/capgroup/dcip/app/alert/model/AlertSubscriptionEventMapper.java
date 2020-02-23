package com.capgroup.dcip.app.alert.model;

import com.capgroup.dcip.app.common.LinkMapper;
import com.capgroup.dcip.app.common.ToLongLink;
import com.capgroup.dcip.app.common.ToUUIDLink;
import com.capgroup.dcip.app.common.UtilityLinkMapper;
import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.alert.AlertSubscriptionEvent;
import com.capgroup.dcip.domain.alert.AlertSubscriptionEventAction;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapping between domain object AlertSubscriptionEvent and DTO AlertSubscriptionEventModel
 */
@Mapper(config = TemporalEntityMapper.class, uses = {LinkMapper.class,
        AlertCriteriaEvaluationMapper.class})
public interface AlertSubscriptionEventMapper {

    @Mapping(source = "entityId", target = "entity", qualifiedBy = {UtilityLinkMapper.class, ToLongLink.class})
    @Mapping(target = "alertType", qualifiedBy = {UtilityLinkMapper.class, ToLongLink.class})
    @Mapping(target = "alertSubscription", qualifiedBy = {UtilityLinkMapper.class, ToLongLink.class})
    @Mapping(target = "eventStatus", source = "alertSubscriptionEventStatus.eventStatus")
    @Mapping(target = "createdTimestamp", source = "alertSpecificationEvent.createdTimestamp")
    @Mapping(target = "evaluationDate", source = "alertSpecificationEvent.evaluationDate")
    @Mapping(target = "alertCriteriaEvaluation", source = "alertSpecificationEvent.alertCriteriaEvaluation")
    @Mapping(target = "alertTypeName", source = "alertType.name")
    @Mapping(target = "message", source = "alertSpecificationEvent.message")
    @Mapping(target = "alertSpecificationName", source = "alertSpecificationEvent.name")
    @Mapping(target = "targetDate", source = "alertSpecificationEvent.targetDate")
    AlertSubscriptionEventModel map(AlertSubscriptionEvent model);

    @Mapping(target = "alertSubscription", ignore = true)
    AlertSubscriptionEvent map(AlertSubscriptionEventModel event);

    @Mappings({@Mapping(target = "entity", source = "entityId", qualifiedBy = {UtilityLinkMapper.class,
            ToLongLink.class}),
            @Mapping(target = "alertSubscriptionEvent", qualifiedBy = {UtilityLinkMapper.class, ToUUIDLink.class}),
            @Mapping(target = "alertActionType", qualifiedBy = {UtilityLinkMapper.class, ToLongLink.class})})
    AlertSubscriptionEventActionModel map(AlertSubscriptionEventAction action);

    List<AlertSubscriptionEventActionModel> mapAllActions(List<AlertSubscriptionEventAction> action);

    @InheritConfiguration(name = "map")
    void update(AlertSubscriptionEventModel model, @MappingTarget AlertSubscriptionEvent target);

    Iterable<AlertSubscriptionEventModel> mapAll(Iterable<AlertSubscriptionEvent> event);
}
