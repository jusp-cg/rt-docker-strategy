package com.capgroup.dcip.domain.data;

import com.capgroup.dcip.domain.identity.Identifiable;
import com.capgroup.dcip.domain.common.Schedule;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class SeriesSchedule implements Identifiable {
    @Id
    @Column(name = "Id")
    private Long id;

    @Embedded
    Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "SeriesId")
    private Series series;
}
