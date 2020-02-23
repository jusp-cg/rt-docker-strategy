package com.capgroup.dcip.domain.entity;

import com.capgroup.dcip.domain.common.Property;
import com.capgroup.dcip.domain.common.PropertyType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.*;

/**
 * Represents a configurable name/value pair. A Property may have meta-data associated with it via PropertyType or
 * can be a dynamically created property
 */
@Table(name = "entity_property_view")
@javax.persistence.Entity
@NoArgsConstructor
@SQLInsert(check = ResultCheckStyle.NONE, callable = true, sql = "{call entity_property_insert(?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?)}")
@SQLUpdate(check = ResultCheckStyle.NONE, callable = true, sql = "{call entity_property_update(?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?, ?)}")
@SQLDelete(check = ResultCheckStyle.NONE, callable = true, sql = "{call entity_property_delete(?, ?)}")
public class EntityProperty extends TemporalEntity implements Property {

    @Column
    @Getter
    @Setter
    String value;

    @Column
    @Getter
    String key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", insertable = false, updatable = false)
    @Getter
    Entity entity;

    @Column(name="entity_id")
    @Getter
    @Setter
    Long entityId;

    @JoinColumn(name = "property_type_id")
    @ManyToOne
    @Getter
    private PropertyType propertyType;

    public EntityProperty(PropertyType propertyType, String value, long entityId) {
        this.propertyType = propertyType;
        this.key = propertyType.getKey();
        this.value = value;
        this.entityId = entityId;
    }

    public EntityProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public EntityProperty(String key) {
        this(key, null);
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
        if (propertyType != null) {
            key = propertyType.getKey();
        }
    }

    @Override
    public <T> T getValueAs(Class<T> type) {
        return propertyType.getValue(this, type);
    }

    @Override
    public void setValueAs(Object obj) {
        propertyType.setValue(this, obj);
    }
}
