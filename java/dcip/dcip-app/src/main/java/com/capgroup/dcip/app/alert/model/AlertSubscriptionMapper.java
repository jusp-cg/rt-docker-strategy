package com.capgroup.dcip.app.alert.model;

import com.capgroup.dcip.app.common.LinkMapper;
import com.capgroup.dcip.app.common.ToEntity;
import com.capgroup.dcip.app.common.ToLongLink;
import com.capgroup.dcip.app.common.UtilityLinkMapper;
import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.alert.AlertSubscription;
import org.mapstruct.*;

@Mapper(config = TemporalEntityMapper.class, uses = {LinkMapper.class})
public interface AlertSubscriptionMapper {
    @Mappings({@Mapping(source = "entityId", target = "entity", qualifiedBy = {UtilityLinkMapper.class, ToLongLink.class}),
            @Mapping(source = "alertType", target = "alertType", qualifiedBy = {UtilityLinkMapper.class, ToLongLink.class}),
            @Mapping(source = "alertSpecification", target = "specification", qualifiedBy = {UtilityLinkMapper.class,
                    ToLongLink.class})})
    AlertSubscriptionModel map(AlertSubscription subscription);

    @Mappings({@Mapping(source = "entity.id", target = "entityId"),
            @Mapping(source = "alertType", target = "alertType", qualifiedBy = {UtilityLinkMapper.class, ToEntity.class})})
    AlertSubscription map(AlertSubscriptionModel model);

    @InheritConfiguration(name = "map")
    AlertSubscription update(AlertSubscriptionModel model,
                             @MappingTarget AlertSubscription subscription);

    Iterable<AlertSubscriptionModel> mapAllAlertSubscriptions(Iterable<AlertSubscription> all);

}
