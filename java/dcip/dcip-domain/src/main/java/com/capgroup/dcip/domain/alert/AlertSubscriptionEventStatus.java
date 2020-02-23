package com.capgroup.dcip.domain.alert;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.*;
import java.util.Arrays;

@Entity
@Table(name = "alert_subscription_event_status_view")
@Data
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call alert_subscription_event_status_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call alert_subscription_event_status_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call alert_subscription_event_status_delete(?, ?)}")
@NoArgsConstructor
public class AlertSubscriptionEventStatus extends TemporalEntity {

    @Column
    @Setter
    @Enumerated
    private Status eventStatus;
    @OneToOne
    @JoinColumn(name = "alert_subscription_event_id")
    private AlertSubscriptionEvent alertSubscriptionEvent;

    public AlertSubscriptionEventStatus(Status status) {
        this.eventStatus = status;
    }

    public enum Status {
        ACKNOWLEDGE,
        READ;

        public static Status of(String status) {
            return Arrays.stream(Status.values()).filter(e -> e.name().equalsIgnoreCase(status)).findAny().orElseThrow(() -> new IllegalArgumentException("unknown status type:" + status));
        }
    }
}
