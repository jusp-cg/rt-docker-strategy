package com.capgroup.dcip.app.data;

import com.capgroup.dcip.domain.reference.company.SymbolType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object that represents a request for a query of a TimeSeries
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSeriesCriteriaModel {
    private Long companyId;
    private long seriesId;
    private Long userId;
    private Long dataSourceId;
    private SymbolType symbolType;
    private String symbol;

    public TimeSeriesCriteriaModel(
            Long companyId,
            long seriesId,
            Long userId,
            Long dataSourceId) {
        this.companyId = companyId;
        this.seriesId = seriesId;
        this.userId = userId;
        this.dataSourceId = dataSourceId;
    }

}
