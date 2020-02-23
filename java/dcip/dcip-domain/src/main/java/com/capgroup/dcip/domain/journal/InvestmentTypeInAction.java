package com.capgroup.dcip.domain.journal;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

@Table(name="investment_type_in_action_view")
@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_type_in_action_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_type_in_action_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_type_in_action_delete(?, ?)}")
public class InvestmentTypeInAction extends TemporalEntity {
    private static final long serialVersionUID = -9070136619794614540L;

    @JoinColumn(name = "investment_action_id")
    @ManyToOne(optional = false)
    private InvestmentAction investmentAction;

    @JoinColumn(name = "investment_type_id")
    @ManyToOne(optional = false)
    private InvestmentType investmentType;

    public InvestmentTypeInAction(InvestmentAction investmentAction, InvestmentType investmentType) {
        this.investmentAction = investmentAction;
        this.investmentType = investmentType;
    }
}
