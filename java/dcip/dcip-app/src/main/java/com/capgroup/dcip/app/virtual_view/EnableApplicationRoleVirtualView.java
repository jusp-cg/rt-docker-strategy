package com.capgroup.dcip.app.virtual_view;

import java.lang.annotation.*;

/**
 * Add to a class/method to enable the queries to the DB to filter those users that are EnableApplicationRoleVirtualView ApplicationRole
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface EnableApplicationRoleVirtualView {
}