package com.capgroup.dcip.domain.journal;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name="investment_reason_in_investment_action_view")
@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_reason_in_investment_action_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_reason_in_investment_action_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_reason_in_investment_action_delete(?, ?)}")
public class InvestmentReasonInInvestmentAction extends TemporalEntity {
    private static final long serialVersionUID = -9070136619794614540L;

    @JoinColumn(name = "investment_action_id")
    @ManyToOne(optional = false)
    private InvestmentAction investmentAction;

    @JoinColumn(name = "investment_reason_id")
    @ManyToOne(optional = false)
    private InvestmentReason investmentReason;

    public InvestmentReasonInInvestmentAction(InvestmentAction investmentAction, InvestmentReason investmentReason) {
        this.investmentAction = investmentAction;
        this.investmentReason = investmentReason;
    }
}

