package com.capgroup.dcip.domain.entity;

import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.domain.event.Event;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.util.UUID;

/**
 * Represents an Entity that has version information and a date range for which
 * that entity was applicable for (ValidPeriod)
 */
@ToString
@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FilterDef(name = "disaggregation2", parameters = {@ParamDef(name = "investmentUnitId", type = "long"),
        @ParamDef(name = "cutOffDate", type = "LocalDateTime")}, defaultCondition = "VersionNo = (SELECT CASE WHEN " +
        "EXISTS(SELECT 1 FROM EntityType et WHERE et.id = entityTypeId AND et.disaggregation = 0) THEN " +
        "(SELECT VersionNo FROM Entity e WHERE e.id = id and e.validEndDate = dbo.maxEndDate()) " +
        "ELSE" +
        "(SELECT MAX(e.versionNo) FROM Entity e JOIN Event evnt ON e.eventId = evnt.id AND e.id = id " +
        "WHERE (v.investmentUnitId = :investmentUnitId OR e.validStartDate < :cutOffDate)" +
        " and status in (0,1)) " +
        "END)")
@FilterDef(name = "pointInTime", parameters = @ParamDef(name = "pointInTime", type = "datetime2"),
        defaultCondition = " (VersionNo = (SELECT e.versionNo from Entity e where :pointInTime between " +
                "e.startDate and e.endDate and e.id = id) and status in (0,1))")
@FilterDef(name = "disaggregationPointInTime", parameters = {@ParamDef(name = "investmentUnitId", type = "long"),
        @ParamDef(name = "pointInTime", type = "LocalDateTime"),
        @ParamDef(name = "cutOffDate", type = "LocalDateTime")}, defaultCondition = "VersionNo = (SELECT CASE WHEN " +
        "EXISTS (SELECT 1 FROM Event evnt WHERE evnt.id = eventId and evnt.investmentUnitId = :investmentUnitId) OR " +
        "EXISTS (SELECT 1 FROM EntityType et where et.id = entityTypeId and et.disaggregation = 0) THEN " +
        "(SELECT e.versionNo FROM Entity e WHERE :pointInTime between e.startDate and e.endDate and e.id = id and " +
        "status in (0,1)) " +
        "ELSE " +
        "(SELECT MAX(e.VersionNo) FROM Entity e JOIN Event evnt ON e.eventId = evnt.id and e.id = id " +
        "WHERE (e.validStartDate < :cutOffDate)) " +
        "END))  AND status in (0,1)")
@Filter(name = "disaggregation2")
@Filter(name = "pointInTime")
@Filter(name = "disaggregationPointInTime")
public abstract class TemporalEntity extends Entity implements Temporal {
    /**
     * Generated Serial Version Id
     */
    private static final long serialVersionUID = -4199362648625676184L;
    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "start", column = @Column(name = "valid_start_date")),
            @AttributeOverride(name = "end", column = @Column(name = "valid_end_date"))})
    private LocalDateTimeRange validPeriod;
    @Column(nullable = false)
    private UUID versionId;
    @Column
    @Getter
    @Setter
    private Status status;

    protected TemporalEntity(long id) {
        super(id);
    }

    protected TemporalEntity(long id, EntityType entityType, Event event, long versionNo, UUID versionId,
                             LocalDateTimeRange validPeriod, Status status) {
        super(id, entityType, event, versionNo);
        this.status = status;
        this.versionId = versionId;
        this.validPeriod = validPeriod;
    }

    /**
     * Extend the base class equals functionality by including the versionId as part of the comparison. The
     * additional check is for retrieving historical entities via projections
     */
    @Override
    public boolean equals(Object rhs) {
        return super.equals(rhs) && (versionId == null || versionId.equals(((TemporalEntity) rhs).versionId));
    }

    public enum Status {ACTIVE, MARKED_FOR_DELETE, DELETED}
}
