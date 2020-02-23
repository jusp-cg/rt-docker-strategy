package com.capgroup.dcip.app.alert.model;

import com.capgroup.dcip.app.common.LinkMapper;
import com.capgroup.dcip.app.common.ToEntity;
import com.capgroup.dcip.app.common.ToLongLink;
import com.capgroup.dcip.app.common.UtilityLinkMapper;
import com.capgroup.dcip.domain.alert.AlertCriteriaEvaluation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapping between an AlertCriteriaEvaluation and its corresponding model
 */
@Mapper(uses = LinkMapper.class)
public abstract class AlertCriteriaEvaluationMapper {
    @Mapping(target = "alertCriteria", qualifiedBy = {UtilityLinkMapper.class, ToLongLink.class})
    @Mapping(target="dataOperator", source="dataOperator.value")
    public abstract AlertCriteriaEvaluationModel map(AlertCriteriaEvaluation model);

    @Mapping(target = "alertCriteria", qualifiedBy = {UtilityLinkMapper.class, ToEntity.class})
    public abstract AlertCriteriaEvaluation map(AlertCriteriaEvaluationModel model);
}
