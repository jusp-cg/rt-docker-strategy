package com.capgroup.dcip.app.virtual_view;

import com.capgroup.dcip.domain.identity.ApplicationRole;
import com.capgroup.dcip.domain.identity.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component("ApplicationRoleVirtualView")
public class ApplicationRoleVirtualView extends AbstractVirtualView {

    @Autowired
    public ApplicationRoleVirtualView(EntityManager entityManager) {
        super(entityManager, "applicationRoleVirtualView");
    }

    @Override
    public boolean canEnable(Profile profile) {
        return super.canEnable(profile)
                && ApplicationRole.ApplicationRoleId.valueOf(profile.getApplicationRoleId())
                != ApplicationRole.ApplicationRoleId.Administrator;
    }

    @Override
    public void enable(Profile profile) {
        session().enableFilter("applicationRoleVirtualView").setParameter("applicationRole",
                profile.getApplicationRoleId());
    }
}
