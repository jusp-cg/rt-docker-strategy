package com.capgroup.dcip.domain.journal;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.identity.User;
import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "partner_in_investment_action_view")
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call partner_in_investment_action_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call partner_in_investment_action_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call partner_in_investment_action_delete(?, ?)}")
public class PartnerInInvestmentAction extends TemporalEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "investment_action_id")
    private InvestmentAction investmentAction;

    @ManyToOne(optional = false)
    @JoinColumn(name = "partner_id")
    private User partner;
}
