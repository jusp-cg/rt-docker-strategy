package com.capgroup.dcip.app.journal;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.common.LinkMapper;
import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.common.ToEntity;
import com.capgroup.dcip.app.common.UtilityLinkMapper;
import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.app.identity.ProfileMapper;
import com.capgroup.dcip.app.reference.capital_system.AccountService;
import com.capgroup.dcip.app.reference.company.CompanyService;
import com.capgroup.dcip.domain.alert.AlertSubscriptionEvent;
import com.capgroup.dcip.domain.identity.User;
import com.capgroup.dcip.domain.journal.InvestmentAction;
import com.capgroup.dcip.infrastructure.repository.ProfileRepository;
import com.capgroup.dcip.infrastructure.repository.UserRepository;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentActionTypeRepository;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentReasonRepository;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentTypeRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsible for converting between an InvestmentAction and an
 * InvestmentActionModel (and vice-versa)
 */
@Mapper(config = TemporalEntityMapper.class, uses = {InvestmentActionTypeMapper.class, ProfileMapper.class,
        InvestmentTypeMapper.class, LinkMapper.class})
public abstract class InvestmentActionMapper {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private InvestmentTypeRepository investmentTypeRepository;

    @Autowired
    private LinkMapper linkMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvestmentActionTypeRepository investmentActionTypeRepository;

    @Autowired
    private InvestmentReasonRepository investmentReasonRepository;

    @Mapping(source = "analystProfile", target = "profile")
    public abstract InvestmentActionModel map(InvestmentAction investmentAction);

    @Mapping(target = "originatorId", source = "originator.id")
    @Mapping(target = "analystProfile", ignore = true)
    @Mapping(target = "investmentActionType", ignore = true)
    @Mapping(target = "investmentTypes", ignore = true)
    @Mapping(target = "accountIds", ignore = true)
    @Mapping(target = "partners", ignore = true)
    @Mapping(target = "investmentReasons", ignore = true)
    public abstract InvestmentAction map(InvestmentActionModel investmentActionModel);

    public abstract Iterable<InvestmentActionModel> toInvestmentActionModels(Iterable<InvestmentAction> InvestmentActions);

    @InheritConfiguration(name = "map")
    public abstract void updateInvestmentAction(InvestmentActionModel investmentActionModel,
                                                @MappingTarget InvestmentAction investmentAction);

    @IterableMapping(qualifiedBy = {UtilityLinkMapper.class, ToEntity.class})
    protected abstract List<User> map(Collection<LinkModel<Long>> items);

    @AfterMapping
    protected void afterMapping(InvestmentAction investmentAction, @MappingTarget InvestmentActionModel model) {
        if (model == null) {
            return;
        }

        // set the originator
        if (investmentAction.getOriginatorId() != null) {
            model.setOriginator(linkMapper.map(investmentAction.getOriginatorId(), AlertSubscriptionEvent.class));
        }

        // set the accounts
        model.setAccounts(
                accountService.findByIds(investmentAction.getAccountIds().stream()).collect(Collectors.toList()));

        // set the listing
        model.setCompany(CompanyService.findByIdOrUnknown(companyService, investmentAction.getCompanyId()));
    }

    @AfterMapping
    protected void afterMapping(InvestmentActionModel model, @MappingTarget InvestmentAction investmentAction) {
        if (investmentAction == null) {
            return;
        }

        if (model.getProfile() != null && model.getProfile().getId() != null) {
            investmentAction.setAnalystProfile(profileRepository.findById(model.getProfile().getId()).get());
        } else {
            investmentAction.setAnalystProfile(null);
        }

        // set the investment action type
        investmentAction.setInvestmentActionType(
                investmentActionTypeRepository.findById(model.getInvestmentActionType().getId()).get());

        // set the listing
        investmentAction.setCompanyId(model.getCompany().getId());

        // remove account ids
        investmentAction.getAccountIds().stream().filter(id -> {
            return !model.getAccounts().stream().anyMatch(y -> id.equals(y.getId()));
        }).forEach(acctId -> investmentAction.removeAccountId(acctId));

        // add the account ids
        model.getAccounts().stream().filter(id -> !investmentAction.getAccountIds().contains(id.getId()))
                .forEach(acctId -> {
                    investmentAction.addAccountId(acctId.getId());
                });

        // remove investment types
        investmentAction.getInvestmentTypes().stream().filter(it -> {
            return !model.getInvestmentTypes().stream().anyMatch(y -> y.getId().equals(it.getId()));
        }).forEach(it -> investmentAction.removeInvestmentType(it.getId()));

        // add investment types
        model.getInvestmentTypes().stream().filter(id -> {
            return !investmentAction.getInvestmentTypes().stream().anyMatch(x -> x.getId().equals(id.getId()));
        }).forEach(it -> investmentAction.addInvestmentType(investmentTypeRepository.findById(it.getId())
                .orElseThrow(() -> new ResourceNotFoundException("InvestmentType", Long.toString(it.getId())))));

        // remove the partners
        investmentAction.getPartners().stream().filter(it -> {
            return !model.getPartners().stream().anyMatch(y -> y.getId().equals(it.getId()));
        }).forEach(it -> investmentAction.removePartner(it.getId()));

        // add the partners
        model.getPartners().stream().filter(id -> {
            return !investmentAction.getPartners().stream().anyMatch(x -> x.getId().equals(id.getId()));
        }).forEach(it -> investmentAction.addPartner(userRepository.findById(it.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner", Long.toString(it.getId())))));

        // remove investment reasons
        investmentAction.getInvestmentReasons().stream().filter(it -> {
            return !model.getInvestmentReasons().stream().anyMatch(y -> y.getId().equals(it.getId()));
        }).forEach(it -> investmentAction.removeInvestmentReason(it.getId()));

        // add investment reasons
        model.getInvestmentReasons().stream().filter(id -> {
            return !investmentAction.getInvestmentReasons().stream().anyMatch(x -> x.getId().equals(id.getId()));
        }).forEach(it -> investmentAction.addInvestmentReason(investmentReasonRepository.findById(it.getId())
                .orElseThrow(() -> new ResourceNotFoundException("InvestmentReason", Long.toString(it.getId())))));

    }
}