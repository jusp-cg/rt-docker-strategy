package com.capgroup.dcip.app.journal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.entity.TemporalEntityModel;
import com.capgroup.dcip.app.identity.ProfileModel;
import com.capgroup.dcip.app.reference.capital_system.AccountModel;
import com.capgroup.dcip.app.reference.company.CompanyModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper=true)
@Data
public class InvestmentActionModel extends TemporalEntityModel {
    private CompanyModel company;
    private ProfileModel profile;
    private List<InvestmentTypeModel> investmentTypes = new ArrayList<>();
    private InvestmentActionTypeModel investmentActionType;
    @Getter(value=AccessLevel.NONE)
    private LocalDateTime actionDate;
    private String publicComment;
    private String orderInfo;
    private String reasonForBuying;
    private List<AccountModel> accounts;
    private boolean isSendMail;
    private LinkModel<UUID> originator;
    private Integer convictionLevel;
    private List<LinkModel<Long>> partners = new ArrayList<>();
    private List<InvestmentReasonModel> investmentReasons = new ArrayList<>();

    public List<InvestmentTypeModel> getInvestmentTypes(){
        return investmentTypes == null ? Collections.emptyList() : investmentTypes;
    }

    public List<LinkModel<Long>> getPartners(){
        return partners == null ? Collections.emptyList() : partners;
    }

    public List<InvestmentReasonModel> getInvestmentReasons(){
        return investmentReasons == null ? Collections.emptyList() : investmentReasons;
    }

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	public LocalDateTime getActionDate() {
		return actionDate;
	}
	
	public void setActionDate(LocalDateTime dt) {
		this.actionDate = dt;
	}

	public Long getCompanyId() { return company == null ? null : company.getId(); }
}
