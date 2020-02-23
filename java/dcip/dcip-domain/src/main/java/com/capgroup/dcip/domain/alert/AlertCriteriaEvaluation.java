package com.capgroup.dcip.domain.alert;

import com.capgroup.dcip.domain.identity.Identifiable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * The result of evaluating an AlertCriteria. This class is not temporal, an evaluation can never be updated/deleted.
 * The class maps to a view, the view provides point in time info for the alert
 */
@Entity
@Table(name = "alert_criteria_evaluation_view")
@EqualsAndHashCode(of = "id")
@SQLInsert(sql = "INSERT INTO alert_criteria_evaluation(alert_criteria_id, alert_criteria_version_id, " +
        "data_operator_evaluation, error_message, evaluation, r_logical_operand_result_id, series_data_value, series_timestamp, " +
        "id) " +
        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", check = ResultCheckStyle.NONE)
@Immutable
public class AlertCriteriaEvaluation implements Identifiable {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
//    // @Type(type = "uuid-char")
    @Getter
    protected UUID id;

    @JoinColumn(name = "alert_criteria_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    private AlertCriteria alertCriteria;

    @Column
    // @Type(type = "uuid-char")
    private UUID alertCriteriaVersionId;
    @JoinColumn(name = "r_logical_operand_result_id")
    @OneToOne(cascade = CascadeType.ALL)
    @Getter
    @Setter
    private AlertCriteriaEvaluation rLogicalOperandEvaluation;
    /**
     * The result of evaluating the data operator
     */
    @Column
    @Getter
    @Setter
    private Boolean dataOperatorEvaluation;
    /**
     * The result of evaluating the associated AlertCriteria and the result of any rLogicalOperands
     */
    @Column
    @Getter
    @Setter
    private Boolean evaluation;
    /**
     * Timestamp of the data value
     */
    @Column
    @Getter
    @Setter
    private LocalDateTime seriesTimestamp;
    /**
     * Data value that was used in the evaluation
     */
    @Column
    @Getter
    @Setter
    private BigDecimal seriesDataValue;
    /**
     * Any error messages that occurred during the evaluation
     */
    @Column
    @Getter
    @Setter
    private String errorMessage;
    /**
     * The dataOperator from the AlertCriteria
     */
    @Column(insertable = false, updatable = false)
    @Getter
    private AlertCriteria.DataOperator dataOperator;
    /**
     * The dataValue from the AlertCriteria
     */
    @Column(insertable = false, updatable = false)
    @Getter
    private BigDecimal dataValue;
    /**
     * The series id from the AlertCriteria
     */
    @Column(insertable = false, updatable = false)
    @Getter
    private long seriesId;
    /**
     * The companyId from the AlertCriteria
     */
    @Column(insertable = false, updatable = false)
    @Getter
    private long companyId;
    /**
     * The logicalOperator from the AlertCriteria
     */
    @Column(insertable = false, updatable = false)
    @Getter
    private AlertCriteria.LogicalOperator rLogicalOperator;

    /**
     * Copies the properties from the AlertCriteria to the Evaluation
     */
    public void setAlertCriteria(AlertCriteria alertCriteria) {
        this.alertCriteria = alertCriteria;
        this.alertCriteriaVersionId = alertCriteria.getVersionId();
        this.companyId = alertCriteria.getCompanyId();
        this.dataOperator = alertCriteria.getDataOperator();
        this.dataValue = alertCriteria.getDataValue();
        this.rLogicalOperator = alertCriteria.getRLogicalOperator();
        this.seriesId = alertCriteria.getSeriesId();
    }

    /**
     * TRUE IFF No error messages and no rLogicalOperandEvaluation errors
     */
    public boolean isSuccess() {
        return errorMessage == null && (rLogicalOperandEvaluation == null || rLogicalOperandEvaluation.isSuccess());
    }

    public boolean isDuplicate(AlertCriteriaEvaluation rhs) {
        return this.alertCriteria.equals(rhs.alertCriteria) &&
                Objects.equals(this.errorMessage, rhs.errorMessage) &&
                Objects.equals(dataOperatorEvaluation, rhs.dataOperatorEvaluation) &&
                Objects.equals(seriesDataValue, rhs.seriesDataValue) &&
                Objects.equals(seriesTimestamp, rhs.seriesTimestamp) &&
                Objects.equals(errorMessage, rhs.errorMessage) &&
                ((this.rLogicalOperandEvaluation == rhs.rLogicalOperandEvaluation) ||
                        (rLogicalOperandEvaluation != null && rLogicalOperandEvaluation.isDuplicate(rhs.rLogicalOperandEvaluation)));
    }
}
