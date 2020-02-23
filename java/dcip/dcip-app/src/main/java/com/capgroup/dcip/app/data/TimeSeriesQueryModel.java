package com.capgroup.dcip.app.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TimeSeriesQueryModel extends TimeSeriesCriteriaModel {
    private String startDate;
    private String endDate;
    private Integer startOffset;

    public TimeSeriesQueryModel(long companyId,
                                long seriesId,
                                String startDate, String endDate, Integer readOffset) {
        super(companyId, seriesId, null, null);
        this.startDate = startDate;
        this.endDate = endDate;
        this.startOffset = readOffset;
    }
    public TimeSeriesQueryModel(long companyId,
                                long seriesId,
                                Long userId,
                                String startDate, String endDate, Integer readOffset) {
        super(companyId, seriesId, userId, null);
        this.startDate = startDate;
        this.endDate = endDate;
        this.startOffset = readOffset;
    }
}
