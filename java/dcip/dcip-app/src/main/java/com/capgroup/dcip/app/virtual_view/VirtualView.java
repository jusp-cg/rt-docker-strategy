package com.capgroup.dcip.app.virtual_view;

import com.capgroup.dcip.domain.identity.Profile;

/**
 * Emanles/Disables a virutal view. A virtual view restricts the queries to the database to show a logical view of
 * the data e.g. only  test data, disaggregated data, etc.
 */
public interface VirtualView {

    boolean canEnable(Profile profile);

    void enable(Profile profile);

    boolean isEnabled();

    void disable();
}
