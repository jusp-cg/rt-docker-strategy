package com.capgroup.dcip.domain.data;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.identity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Table(name = "user_time_series_view")
@SQLInsert(check = ResultCheckStyle.NONE, callable = true, sql = "{call user_time_series_insert(?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?)}")
@SQLUpdate(check = ResultCheckStyle.NONE, callable = true, sql = "{call user_time_series_update(?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?)}")
@SQLDelete(check = ResultCheckStyle.NONE, callable = true, sql = "{call user_time_series_delete(?, ?)}")
public class UserTimeSeries extends TemporalEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "Company_id")
    private long companyId;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;
}
