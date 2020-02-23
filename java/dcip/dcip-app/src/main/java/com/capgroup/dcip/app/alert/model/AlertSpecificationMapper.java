package com.capgroup.dcip.app.alert.model;

import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.alert.AlertCriteria;
import com.capgroup.dcip.domain.alert.AlertSpecification;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.stream.Stream;

/**
 * Mapping between AlertSpecificationEvent and AlertSpecificationModel
 */
@Mapper(config = TemporalEntityMapper.class)
public abstract class AlertSpecificationMapper {

    @Value("${application.url}")
    private String resourceUrl;

    public abstract AlertSpecificationModel map(AlertSpecification alert);

    public abstract AlertSpecification map(AlertSpecificationModel model);

    public abstract Iterable<AlertSpecificationModel> mapAllAlertSpecifications(Iterable<AlertSpecification> all);

    @InheritConfiguration(name = "map")
    public abstract void update(AlertSpecificationModel model, @MappingTarget AlertSpecification spec);

    @InheritConfiguration(name = "map")
    public abstract void update(AlertCriteriaModel model, @MappingTarget AlertCriteria criteria);

    @Mapping(source = "dataValue", target = "value")
    @Mapping(source = "dataOperator.value", target = "operator")
    public abstract AlertCriteriaModel map(AlertCriteria criteria);

    @Mapping(source = "value", target = "dataValue")
    @Mapping(source = "operator", target = "dataOperator", qualifiedByName = "dataOperator")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract AlertCriteria map(AlertCriteriaModel model);

    @Named("dataOperator")
    protected AlertCriteria.DataOperator dataOperator(String operator) {
        return Stream.of(AlertCriteria.DataOperator.values())
                .filter(x -> x.getValue().equals(operator)).findAny().orElse(null);
    }

    /**
     * set the reference to the subscriptions
     */
    @AfterMapping
    protected void afterMapping(AlertSpecification specification,
                                @MappingTarget AlertSpecificationModel model) {
        LinkModel linkModel = new LinkModel();
        linkModel.setRef(resourceUrl + "/api/dcip/alert/subscriptions?specificationId=" + specification.getId());
        model.setSubscriptions(linkModel);
    }

    @AfterMapping
    protected void afterMapping(AlertCriteriaModel model, @MappingTarget AlertCriteria criteria) {
        AlertCriteriaModel childModel;
        if (model.getAnd() != null) {
            childModel = model.getAnd();
            criteria.setRLogicalOperator(AlertCriteria.LogicalOperator.AND);
        } else if (model.getOr() != null) {
            childModel = model.getOr();
            criteria.setRLogicalOperator(AlertCriteria.LogicalOperator.OR);
        } else if (model.getNot() != null) {
            childModel = model.getNot();
            criteria.setRLogicalOperator(AlertCriteria.LogicalOperator.NOT);
        } else {
            return;
        }
        criteria.setRLogicalOperand(map(childModel));
    }

    @AfterMapping
    protected void afterMapping(AlertCriteria criteria, @MappingTarget AlertCriteriaModel model) {
        if (criteria.getRLogicalOperand() != null) {
            switch (criteria.getRLogicalOperator()) {
                case AND:
                    model.setAnd(map(criteria.getRLogicalOperand()));
                    break;
                case OR:
                    model.setOr(map(criteria.getRLogicalOperand()));
                    break;
                case NOT:
                    model.setNot(map(criteria.getRLogicalOperand()));
            }
        }
    }
}
