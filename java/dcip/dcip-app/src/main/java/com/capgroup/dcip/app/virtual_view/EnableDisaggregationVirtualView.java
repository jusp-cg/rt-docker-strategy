package com.capgroup.dcip.app.virtual_view;

import java.lang.annotation.*;

/**
 * Add to a class/method to enable the queries to the DB to filter by investment unit
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface EnableDisaggregationVirtualView {
}
