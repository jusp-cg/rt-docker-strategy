package com.capgroup.dcip.app.virtual_view;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface EnableAllVirtualViews {
}
