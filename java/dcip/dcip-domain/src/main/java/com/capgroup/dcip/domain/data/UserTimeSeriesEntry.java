package com.capgroup.dcip.domain.data;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user_time_series_entry_view")
@EqualsAndHashCode
@SQLInsert(check = ResultCheckStyle.NONE, callable = true, sql = "{call user_time_series_entry_insert(?, ?, ?, ?, ?, ?, ?," +
        " ?, ?, ?, ?)}")
@SQLUpdate(check = ResultCheckStyle.NONE, callable = true, sql = "{call user_time_series_entry_update(?, ?, ?, ?, ?, ?, ?," +
        " ?, ?, ?, ?, ?)}")
@SQLDelete(check = ResultCheckStyle.NONE, callable = true, sql = "{call user_time_series_entry_delete(?, ?)}")
public class UserTimeSeriesEntry extends TemporalEntity {
    @Column(nullable = false)
    @Getter
    private LocalDateTime timestamp;

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal value;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_time_series_id")
    @Getter
    private UserTimeSeries userTimeSeries;

    public TimeSeries.Entry toTimeSeriesEntry(){
        return new TimeSeries.Entry(timestamp, value);
    }
}
