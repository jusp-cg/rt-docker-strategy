package com.capgroup.dcip.app.entity;

import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Data transfer object that represents a com.capgroup.dcip.domain.TemporalEntity
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor()
public abstract class TemporalEntityModel extends EntityModel {
    private LocalDateTimeRange validPeriod;
    private TemporalEntity.Status status;
}
