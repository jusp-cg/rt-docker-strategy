package com.capgroup.dcip.app.journal;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.alert.service.AlertSubscriptionEventService;
import com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews;
import com.capgroup.dcip.domain.journal.InvestmentAction;
import com.capgroup.dcip.domain.journal.QInvestmentAction;
import com.capgroup.dcip.infrastructure.repository.EntityRepository;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentActionRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * Implementation that provides methods related to InvestmentActions
 */
@Service
@EnableAllVirtualViews
public class InvestmentActionServiceImpl implements InvestmentActionService {

    InvestmentActionRepository investmentActionRepository;
    InvestmentActionMapper investmentActionMapper;
    EntityManager entityManager;
    AlertSubscriptionEventService alertSubscriptionEventService;

    @Autowired
    public InvestmentActionServiceImpl(InvestmentActionRepository investmentActionRepository,
                                       InvestmentActionMapper investmentActionMapper, EntityManager entityManager,
                                       AlertSubscriptionEventService alertSubscriptionEventService) {
        this.investmentActionRepository = investmentActionRepository;
        this.investmentActionMapper = investmentActionMapper;
        this.entityManager = entityManager;
        this.alertSubscriptionEventService = alertSubscriptionEventService;
    }

    private static BooleanExpression getProfileExpression(Long profileId) {
        return new EntityRepository.ExpressionBuilder(QInvestmentAction.investmentAction._super._super).hasProfile(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<InvestmentActionModel> findAll(Long profileId) {
        Iterable<InvestmentAction> investmentActions = investmentActionRepository
                .findAllByEventProfileId(profileId);
        return investmentActionMapper.toInvestmentActionModels(investmentActions);
    }

    @Override
    @Transactional(readOnly = true)
    public InvestmentActionModel findById(long id) {
        return investmentActionRepository.findById(id).map(this::buildModel).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<InvestmentActionModel> findByCompanyIdAndProfileId(long securityId, Long profileId) {
        Iterable<InvestmentAction> investmentActions = investmentActionRepository
                .findAll(getProfileExpression(profileId).and(QInvestmentAction.investmentAction.companyId.eq(securityId)));
        return investmentActionMapper.toInvestmentActionModels(investmentActions);
    }

    @Override
    @Transactional
    public InvestmentActionModel create(InvestmentActionModel investmentActionModel) {
        InvestmentAction investmentAction = investmentActionMapper.map(investmentActionModel);
        InvestmentAction result = investmentActionRepository.save(investmentAction);

        // Synchronize the persistence context to the underlying database.
        entityManager.flush();

        // if the investment action was created due to an alert then an alert action needs to be created
        if (result.getOriginatorId() != null) {
            alertSubscriptionEventService.createAction(result.getOriginatorId(), investmentAction.getId(), 1);
        }

        return buildModel(result);
    }

    @Override
    @Transactional
    public InvestmentActionModel update(long id, InvestmentActionModel investmentActionModel) {
        InvestmentAction investmentAction = investmentActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InvestmentAction", Long.toString(id)));

        investmentActionMapper.updateInvestmentAction(investmentActionModel, investmentAction);
        InvestmentAction result = investmentActionRepository.save(investmentAction);

        // Synchronize the persistence context to the underlying database.
        entityManager.flush();

        return buildModel(result);
    }

    @Override
    @Transactional
    public void delete(long id) {
        InvestmentAction investmentAction = investmentActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InvestmentAction", Long.toString(id)));
        investmentActionRepository.delete(investmentAction);
    }

    private InvestmentActionModel buildModel(InvestmentAction investmentAction) {
        return investmentActionMapper.map(investmentAction);
    }
}
