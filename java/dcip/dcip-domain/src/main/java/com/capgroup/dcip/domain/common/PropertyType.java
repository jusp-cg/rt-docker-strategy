package com.capgroup.dcip.domain.common;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
@Data
public class PropertyType {
    @Enumerated
    DataType dataType;

    @Id
    @Column(name = "Id")
    private long id;

    @Column(name = "Key")
    private String key;

    @Column(name = "Description")
    private String description;

    public void setValue(Property property, Object value) {
        dataType.setValue(property, value);
    }

    public <T> T getValue(Property property, Class<T> type) {
        return dataType.getValue(property, type);
    }
}
