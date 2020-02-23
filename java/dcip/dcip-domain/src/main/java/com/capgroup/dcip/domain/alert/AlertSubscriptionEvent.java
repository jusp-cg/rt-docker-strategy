package com.capgroup.dcip.domain.alert;

import com.capgroup.dcip.domain.identity.Identifiable;
import com.querydsl.core.annotations.QueryInit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "alert_subscription_event_view")
@Getter
@NoArgsConstructor
@Immutable
@SQLInsert(sql = "INSERT INTO alert_subscription_event (alert_specification_event_id, alert_subscription_id, " +
        "alert_subscription_version_id, id) values (?, ?, ?, ?)")
@Filter(name = "disaggregationVirtualView",
        condition = "EXISTS(SELECT 1 FROM disaggregation_entity_view dev " +
                "JOIN alert_subscription_view asv ON dev.version_id = asv.version_id AND dev.entity_id = asv.id " +
                "WHERE dev.investment_unit_id = :investmentUnitId and asv.id = alert_subscription_id)")
/*@Filter(name = "applicationRoleVirtualView",
        condition = " COALESCE((SELECT p.ApplicationRoleId FROM dbo.ProfileView p " +
                "JOIN dbo.Event e ON e.profileId = p.id " +
                "JOIN dbo.AlertSubscriptionView asv on e.id = asv.eventId " +
                "WHERE asv.id = alertSubscriptionId), :applicationRole) = :applicationRole")*/
public class AlertSubscriptionEvent implements Identifiable<UUID> {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    // @Type(type = "uuid-char")
    private UUID id;

    @Column
    // @Type(type = "uuid-char")
    private UUID alertSubscriptionVersionId;

    @ManyToOne
    @JoinColumn(name = "alert_subscription_id", nullable = false)
    @QueryInit("event.profile")
    private AlertSubscription alertSubscription;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "alert_specification_event_id", nullable = false)
    private AlertSpecificationEvent alertSpecificationEvent;

    @OneToMany(mappedBy = "alertSubscriptionEvent", cascade = CascadeType.ALL)
    private List<AlertSubscriptionEventAction> alertSubscriptionEventActions;

    @OneToOne(mappedBy = "alertSubscriptionEvent", cascade = CascadeType.ALL)
    private AlertSubscriptionEventStatus alertSubscriptionEventStatus;

    // fields from the AlertSubscription
    @ManyToOne
    @JoinColumn(name = "alert_type_id", insertable = false)
    @Getter
    private AlertType alertType;

    @Column(insertable = false)
    @Getter
    private long entityId;

    public AlertSubscriptionEvent(AlertSubscription subscription,
                                  AlertSpecificationEvent specificationEvent) {
        setAlertSubscription(subscription);
        this.alertSpecificationEvent = specificationEvent;
    }

    public void setAlertSubscriptionEventStatus(AlertSubscriptionEventStatus status) {
        status.setAlertSubscriptionEvent(this);
        this.alertSubscriptionEventStatus = status;
    }

    public void setAlertSubscription(AlertSubscription subscription) {
        this.alertSubscription = subscription;
        this.alertSubscriptionVersionId = subscription.getVersionId();
        this.alertType = subscription.getAlertType();
        this.entityId = subscription.getEntityId();
    }

    public Boolean getEvaluationResult() {
        return alertSpecificationEvent.getEvaluationResult();
    }

    public boolean isSuccess() {
        return alertSpecificationEvent.isSuccess();
    }

    public void addAlertSubscriptionAction(AlertSubscriptionEventAction action) {
        action.setAlertSubscriptionEvent(this);
        this.alertSubscriptionEventActions.add(action);
    }
}
