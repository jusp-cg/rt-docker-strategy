package com.capgroup.dcip.app.virtual_view;

import com.capgroup.dcip.domain.identity.ApplicationRole;
import com.capgroup.dcip.domain.identity.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component("DisaggregationVirtualView")
public class DisaggregationVirtualView extends AbstractVirtualView {

    @Autowired
    public DisaggregationVirtualView(EntityManager entityManager) {
        super(entityManager, "disaggregationVirtualView");
    }

    @Override
    public boolean canEnable(Profile profile) {
        return super.canEnable(profile) && profile.getUser().getInvestmentUnit() != null
                && ApplicationRole.ApplicationRoleId.valueOf(profile.getApplicationRoleId())
                != ApplicationRole.ApplicationRoleId.Administrator;
    }

    @Override
    public void enable(Profile profile) {
        session().enableFilter("disaggregationVirtualView").setParameter("investmentUnitId",
                profile.getUser().getInvestmentUnit().getId());
    }
}
