package com.capgroup.dcip.domain.alert;

import com.capgroup.dcip.domain.identity.Identifiable;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
public class AlertActionType implements Identifiable, Comparable<AlertActionType> {
    @Id
    @Column
    private Long id;

    @Column
    private int orderBy;

    @Column
    private String name;

    @Column
    private String description;

    @Override
    public int compareTo(AlertActionType o) {
        return Integer.compare(orderBy, o.orderBy);
    }
}
