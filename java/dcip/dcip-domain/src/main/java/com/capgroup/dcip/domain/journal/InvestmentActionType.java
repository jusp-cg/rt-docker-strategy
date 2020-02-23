package com.capgroup.dcip.domain.journal;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.*;

@Entity
@Table(name = "investment_action_type_view")
@NoArgsConstructor
@Getter
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_action_type_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_action_type_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_action_type_delete(?, ?)}")
public class InvestmentActionType extends TemporalEntity {
    /**
     * Generated serial version no
     */
    private static final long serialVersionUID = -3257094511992940364L;

    @Column
    private String investmentAction;
    
    @Column
    private int orderBy;

    @Column
    private Boolean enableEmail;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    @Setter
    private InvestmentActionTypeGroup group;

    public InvestmentActionType(long id, String investmentAction, int order, boolean enableEmail) {
        super(id);
        this.investmentAction = investmentAction;
        this.orderBy = order;
        this.enableEmail = enableEmail;
    }

    public InvestmentActionType(String investmentAction, int order, Boolean enableEmail) {
        this(0, investmentAction, order, enableEmail);
    }
}
