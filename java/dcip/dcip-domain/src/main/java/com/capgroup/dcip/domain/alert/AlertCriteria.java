package com.capgroup.dcip.domain.alert;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "alert_criteria_view")
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call alert_criteria_insert(?, ?, ?, ?, ?, ?, ?, ?, ?," +
        " ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call alert_criteria_update(?, ?, ?, ?, ?, ?, ?, ?, ?," +
        " ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call alert_criteria_delete(?, ?)}")
public class AlertCriteria extends TemporalEntity {

    private static final long serialVersionUID = -6274343128881344648L;
    @Column
    private Long seriesId;
    @Column
    private Long companyId;
    @Enumerated
    @Column
    private DataOperator dataOperator;
    @Column
    private BigDecimal dataValue;
    @Enumerated
    @Column
    private LogicalOperator rLogicalOperator;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "r_logical_operand_id")
    private AlertCriteria rLogicalOperand;

    public enum DataOperator {
        LESS_THAN("<"), GREATER_THAN(">"), EQUAL_TO("="), LESS_THAN_OR_EQUAL_TO("<="), GREATER_THAN_OR_EQUAL_TO(">=")
        , NOT_EQUAL_TO(
                "!=");

        @Getter
        private String value;

        DataOperator(String value) {
            this.value = value;
        }
    }

    public enum LogicalOperator {
        AND, NOT, OR;
    }
}
