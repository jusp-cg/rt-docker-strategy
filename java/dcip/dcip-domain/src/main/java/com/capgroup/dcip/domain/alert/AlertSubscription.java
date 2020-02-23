package com.capgroup.dcip.domain.alert;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created when a user subscribes to a AlertSpecificationEvent Ownership is derived from
 * the event that create the subscription temporal - users can change a
 * subscription to be active/inactive or delete
 */
@Entity
@Getter
@Setter
@Table(name = "alert_subscription_view")
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call alert_subscription_insert(?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call alert_subscription_update(?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call alert_subscription_delete(?, ?)}")
public class AlertSubscription extends TemporalEntity {

    private static final long serialVersionUID = -4956512524914147070L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_type_id")
    private AlertType alertType;

    @Column
    private boolean active;

    @Column
    private long entityId;

    @JoinColumn(name = "alert_specification_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AlertSpecification alertSpecification;

    public AlertSpecification getAlertSpecification(){
        return alertSpecification;
    }

    public void setAlertSpecification(AlertSpecification s){
        this.alertSpecification = s;
    }

    @Column(insertable = false, updatable = false)
    private LocalDateTime activeTimestampChange;

    public AlertSubscriptionEvent createEvent(AlertSpecificationEvent event){
        return new AlertSubscriptionEvent(this, event);
    }
}
