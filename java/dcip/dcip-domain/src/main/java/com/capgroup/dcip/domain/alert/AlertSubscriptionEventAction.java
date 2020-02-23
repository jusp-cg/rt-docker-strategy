package com.capgroup.dcip.domain.alert;

import com.capgroup.dcip.domain.identity.Identifiable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;

/**
 * Represents an action that a user performs on an alert event
 */
@Entity
@Getter
@NoArgsConstructor
public class AlertSubscriptionEventAction implements Identifiable<UUID> {
    @JoinColumn(name = "alert_subscription_event_id")
    @ManyToOne(optional = false)
    @Setter
    private AlertSubscriptionEvent alertSubscriptionEvent;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    // @Type(type = "uuid-char")
    private UUID id;

    @JoinColumn(name = "alert_action_type_id")
    @ManyToOne(optional = false)
    private AlertActionType alertActionType;

    @Column
    private long entityId;

    public AlertSubscriptionEventAction(AlertActionType alertActionType,
                                        long entityId) {
        this.alertActionType = alertActionType;
        this.entityId = entityId;
    }
}
