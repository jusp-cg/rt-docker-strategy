package com.capgroup.dcip.domain.common;

import com.capgroup.dcip.util.ConverterUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public enum DataType {
    INTEGER(Integer.class),
    LONG(Long.class),
    STRING(String.class),
    DATE(LocalDate.class),
    TIMESTAMP(LocalDateTime.class),
    DATE_TIMESTAMP(LocalDateTime.class),
    DECIMAL(BigDecimal.class),
    BOOLEAN(Boolean.class);

    Class<?> classType;

    DataType(Class<?> classType) {
        this.classType = classType;
    }

    public void setValue(Property property, Object value) {
        property.setValue(ConverterUtils.convertTo(value, String.class));
    }

    public <T> T getValue(Property property, Class<T> type) {
        return ConverterUtils.convertTo(property.getValue(), type);
    }
}
