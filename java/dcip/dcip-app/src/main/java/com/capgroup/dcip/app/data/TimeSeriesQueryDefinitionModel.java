package com.capgroup.dcip.app.data;

import com.capgroup.dcip.app.entity.TemporalEntityModel;
import com.capgroup.dcip.app.reference.company.CompanyModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object that represents the temporal domain object
 * TimeSeriesQueryDefinition
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TimeSeriesQueryDefinitionModel extends TemporalEntityModel {
    private String startDate;
    private String endDate;
    private String name;
    private CompanyModel company;
    private SeriesModel series;

    public TimeSeriesQueryModel toTimeSeriesQueryModel() {
        return new TimeSeriesQueryModel(company.getId(),
                series.getId(), startDate, endDate, null);
    }
}
