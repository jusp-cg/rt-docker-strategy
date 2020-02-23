package com.capgroup.dcip.domain.alert;

import com.capgroup.dcip.domain.identity.Identifiable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

/**
 * Type of alert e.g. Content/Market Data
 */
@Entity
@Getter
@EqualsAndHashCode
public class AlertType implements Identifiable, Comparable<AlertType> {
    @Column
    int orderBy;

    @JoinTable(name = "alert_action_type_in_alert_type",
            joinColumns = @JoinColumn(name = "alert_type_id"),
            inverseJoinColumns = @JoinColumn(name = "alert_action_type_id"))
    @OneToMany
    @OrderBy("order_by")
    private List<AlertActionType> alertActionTypes;

    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String messageTemplate;

    @Override
    public int compareTo(AlertType o) {
        return Integer.compare(orderBy, o.orderBy);
    }
}
