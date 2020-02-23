package com.capgroup.dcip.domain.journal;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "investment_action_view")
@Getter
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_action_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_action_update(?, ?, ?, ?, ?::timestamp, ?::timestamp, ?, ?::timestamp, ?, ?, ?::smallint, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_action_delete(?, ?)}")
public class InvestmentAction extends TemporalEntity {
    /**
     * Generated serial version no
     */
    private static final long serialVersionUID = 5394812130995869266L;

    @Column
    @NotNull
    @Setter
    LocalDateTime actionDate;

    @Column
    @Setter
    String publicComment;

    @Column
    @Setter
    String orderInfo;

    @Column
    @Setter
    String reasonForBuying;

    @Column
    @Setter
    boolean isSendMail = false;

    @Column
    @NotNull
    @Setter
    long companyId;

    @OneToMany(mappedBy = "investmentAction", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<InvestmentTypeInAction> investmentTypesInActions = new HashSet<>();

    @OneToMany(mappedBy = "investmentAction", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<AccountInInvestmentAction> accountsInActions = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "investment_action_type_id")
    @Setter
    InvestmentActionType investmentActionType;

    @ManyToOne
    @Setter
    @JoinColumn(name = "analyst_profile_id")
    Profile analystProfile;

    @OneToMany(mappedBy = "investmentAction", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<PartnerInInvestmentAction> partnersInInvestmentAction = new HashSet<>();

    @OneToMany(mappedBy = "investmentAction", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<InvestmentReasonInInvestmentAction> investmentReasonInInvestmentAction= new HashSet<>();

    @Column
    @Setter
    UUID originatorId;

    @Column
    @Setter
    Short convictionLevel;

    public InvestmentAction() {
    }

    public InvestmentAction(LocalDateTime actionDate, String publicComment, String orderInfo, String reasonForBuying,
                            InvestmentActionType actionType, Profile profile, long companyId, boolean isSendMail, Short convictionLevel) {
        this(0, actionDate, publicComment, orderInfo, reasonForBuying, actionType, profile, companyId, isSendMail, convictionLevel);
    }

    public InvestmentAction(long id, LocalDateTime actionDate, String publicComment, String orderInfo,
                            String reasonForBuying,
                            InvestmentActionType actionType, Profile profile, long companyId, boolean isSendMail, Short convictionLevel) {
        super(id);
        this.actionDate = actionDate;
        this.publicComment = publicComment;
        this.orderInfo = orderInfo;
        this.analystProfile = profile;
        this.isSendMail = isSendMail;
        this.investmentActionType = actionType;
        this.reasonForBuying = reasonForBuying;
        this.companyId = companyId;
        this.convictionLevel = convictionLevel;
    }

    public void addInvestmentType(InvestmentType investmentType) {
        investmentTypesInActions.add(new InvestmentTypeInAction(this, investmentType));
    }

    public Collection<InvestmentType> getInvestmentTypes() {
        return investmentTypesInActions.stream().map(InvestmentTypeInAction::getInvestmentType).distinct().collect(Collectors.toList());
    }

    public void addAccountId(long accountUid) {
        accountsInActions.add(new AccountInInvestmentAction(this, accountUid));
    }

    public Collection<Long> getAccountIds() {
        return accountsInActions.stream().map(AccountInInvestmentAction::getPortfolioUid).distinct().collect(Collectors.toList());
    }

    public void removeAccountId(long accountId) {
        accountsInActions.removeIf(x -> x.getPortfolioUid() == accountId);
    }

    public void removeInvestmentType(long id) {
        investmentTypesInActions.removeIf(x -> x.getInvestmentType().getId() == id);
    }

    public Collection<User> getPartners(){
        return partnersInInvestmentAction.stream().map(x->x.getPartner()).collect(Collectors.toList());
    }

    public void removePartner(long id) {
        partnersInInvestmentAction.removeIf(x->x.getPartner().getId() == id);
    }

    public void addPartner(User user){
        partnersInInvestmentAction.add(new PartnerInInvestmentAction(this, user));
    }


    public void addInvestmentReason(InvestmentReason investmentReason) {
        investmentReasonInInvestmentAction.add(new InvestmentReasonInInvestmentAction(this, investmentReason));
    }

    public Collection<InvestmentReason> getInvestmentReasons() {
        return investmentReasonInInvestmentAction.stream().map(InvestmentReasonInInvestmentAction::getInvestmentReason).distinct().collect(Collectors.toList());
    }

    public void removeInvestmentReason(long id) {
        investmentReasonInInvestmentAction.removeIf(x -> x.getInvestmentReason().getId() == id);
    }
}
