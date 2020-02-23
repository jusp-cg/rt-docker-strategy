package com.capgroup.dcip.domain.common;

public interface Property {
    <T> T getValueAs(Class<T> type);

    void setValueAs(Object obj);

    String getValue();

    void setValue(String str);

    PropertyType getPropertyType();
}
