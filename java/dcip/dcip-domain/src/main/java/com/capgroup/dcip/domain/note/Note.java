package com.capgroup.dcip.domain.note;

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
@Table(name = "note_view")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call note_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call note_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call note_delete(?, ?)}")
public class Note extends TemporalEntity {

    private static final long serialVersionUID = -2769370100845270727L;

    @Column
    private String summary;

    @Column
    private String detail;

    /**
     * Constructor used in projection to set the detail
     */
    public Note(long id, EntityType entityType, Event event, long versionNo, UUID versionId,
                LocalDateTimeRange validPeriod, Status status, String detail) {
        super(id, entityType, event, versionNo, versionId, validPeriod, status);
        this.detail = detail;
    }

    /**
     * Constructor used in projection to set the summary
     */
    public Note(long id, EntityType entityType, Event event, long versionNo, UUID versionId,
                LocalDateTimeRange validPeriod, String summary, Status status) {
        super(id, entityType, event, versionNo, versionId, validPeriod, status);
        this.summary = summary;
    }
}
