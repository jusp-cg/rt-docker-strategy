package com.capgroup.dcip.app.virtual_view;

import com.capgroup.dcip.domain.identity.Profile;
import org.hibernate.Session;

import javax.persistence.EntityManager;

public abstract class AbstractVirtualView implements VirtualView {
    private EntityManager entityManager;
    private String filter;

    protected AbstractVirtualView(EntityManager entityManager,
                                  String filter) {
        this.entityManager = entityManager;
        this.filter = filter;
    }

    @Override
    public boolean canEnable(Profile profile) {
        return !isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return session().getEnabledFilter(filter) != null;
    }

    @Override
    public void disable() {
        session().disableFilter(filter);
    }

    protected Session session() {
        return (Session) entityManager.getDelegate();
    }
}
