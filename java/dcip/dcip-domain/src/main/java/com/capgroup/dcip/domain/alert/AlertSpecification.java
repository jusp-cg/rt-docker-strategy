package com.capgroup.dcip.domain.alert;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Entity
@Table(name = "alert_specification_view")
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call alert_specification_insert(?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call alert_specification_update(?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call alert_specification_delete(?, ?)}")
public class AlertSpecification extends TemporalEntity {

    private static final long serialVersionUID = -5422914895651384406L;

    @Setter
    @Column
    private String name;

    @Column
    private boolean active;

    /**
     * Deactivate all the Subscriptions
     */
    public void setActive(boolean value){
        if (active != value && !value){
            alertSubscriptions.forEach(x->x.setActive(false));
        }
        active = value;
    }

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "alert_criteria_id", nullable = false)
    @Setter
    private AlertCriteria alertCriteria;

    @Setter
    @Column
    private LocalDate targetDate;

    @OneToMany(mappedBy = "alertSpecification", cascade = CascadeType.ALL)
    private Set<AlertSubscription> alertSubscriptions = new HashSet<>();

    @Column(insertable = false, updatable = false)
    @Getter
    private LocalDateTime activeTimestampChange;

    public void addAlertSubscription(AlertSubscription subscription) {
        subscription.setAlertSpecification(this);
        alertSubscriptions.add(subscription);
    }

    public Optional<AlertSubscription> removeAlertSubscription(long id) {
        return alertSubscriptions.stream().filter(x -> x.getId().equals(id)).findAny().map(x -> {
            alertSubscriptions.remove(x);
            return x;
        });
    }
}
