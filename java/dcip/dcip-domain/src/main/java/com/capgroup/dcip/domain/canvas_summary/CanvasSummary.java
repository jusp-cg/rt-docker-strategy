package com.capgroup.dcip.domain.canvas_summary;

import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.domain.entity.EntityType;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "canvas_summary_view")
@Data
@NoArgsConstructor
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call canvas_summary_insert(?, ?, ?, ?, ?, ?, ?, ?, ?," +
		" ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call canvas_summary_update(?, ?, ?, ?, ?, ?, ?, ?, ?," +
		" ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call canvas_summary_delete(?, ?)}")
public class CanvasSummary extends TemporalEntity {

    @Column
    private String tagline;

    @Column
    private String action;

    /**
     * Constructor used in projection to set the tagline
     */
    public CanvasSummary(long id, EntityType entityType, Event event, long versionNo, UUID versionId,
                         LocalDateTimeRange validPeriod, Status status, String tagline) {
        super(id, entityType, event, versionNo, versionId, validPeriod, status);
        this.tagline = tagline;
    }

    /**
     * Constructor used in projection to set the action
     */
    public CanvasSummary(long id, EntityType entityType, Event event, long versionNo, UUID versionId,
						 LocalDateTimeRange validPeriod, String action, Status status) {
        super(id, entityType, event, versionNo, versionId, validPeriod, status);
        this.action = action;
    }
}
