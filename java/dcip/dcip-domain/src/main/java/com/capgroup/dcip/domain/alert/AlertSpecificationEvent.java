package com.capgroup.dcip.domain.alert;

import com.capgroup.dcip.domain.identity.Identifiable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an event for an AlertSpecification and an AlertSubscription
 */

@Entity
@Data
@Table(name = "alert_specification_event_view")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Immutable
@SQLInsert(sql = "INSERT INTO alert_specification_event (alert_criteria_evaluation_id, alert_specification_id, " +
        "alert_specification_version_id, created_timestamp, evaluation_date, evaluation_result, message, id) values (?, ?, " +
        "?, ?, ?, ?, ?, ?)", check = ResultCheckStyle.NONE)
@Filter(name = "disaggregationVirtualView",
        condition = "EXISTS(SELECT 1 FROM disaggregation_entity_view dev " +
                "JOIN alert_specification_view asv ON dev.version_id = asv.version_id AND dev.entity_id = asv.id " +
                "WHERE dev.investment_unit_id = :investmentUnitId and asv.id = alert_specification_id)")
/*@Filter(name = "applicationRoleVirtualView",
        condition = " COALESCE((SELECT p.ApplicationRoleId FROM dbo.ProfileView p " +
                "JOIN dbo.Event e ON e.profileId = p.id " +
                "JOIN dbo.AlertSpecificationView asv on e.id = asv.eventId " +
                "WHERE asv.id = alertSpecificationId), :applicationRole) = :applicationRole")*/
public class AlertSpecificationEvent implements Identifiable<UUID> {
    @ManyToOne
    @JoinColumn(name = "alert_specification_id")
    private AlertSpecification alertSpecification;

    @Column
    // @Type(type = "uuid-char")
    private UUID alertSpecificationVersionId;

    @Column
    private LocalDateTime createdTimestamp;

    @JoinColumn(name = "alert_criteria_evaluation_id")
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private AlertCriteriaEvaluation alertCriteriaEvaluation;

    @Column
    private Boolean evaluationResult;

    @Column
    private LocalDateTime evaluationDate;
    @Column(nullable = false)
    @Setter
    private String message;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    // @Type(type = "uuid-char")
    private UUID id;
    @Column(insertable = false)
    private LocalDate targetDate;
    @Column(insertable = false)
    private String name;

    public AlertSpecificationEvent(UUID id) {
        this.id = id;
    }

    public boolean isSuccess() {
        return alertCriteriaEvaluation.isSuccess();
    }

    public void setAlertSpecification(AlertSpecification spec) {
        this.alertSpecification = spec;
        this.alertSpecificationVersionId = spec.getVersionId();
        this.targetDate = spec.getTargetDate();
        this.name = spec.getName();
    }

    public boolean isDuplicate(AlertSpecificationEvent event) {
        if (event == null)
            return false;

        return this.alertSpecification.equals(event.getAlertSpecification()) &&
                Objects.equals(message, event.message) &&
                Objects.equals(evaluationResult, event.evaluationResult) &&
                Objects.equals(evaluationDate, event.evaluationDate) &&
                ((alertCriteriaEvaluation == event.alertCriteriaEvaluation) ||
                        (alertCriteriaEvaluation != null && alertCriteriaEvaluation.isDuplicate(event.alertCriteriaEvaluation)));
    }

}
