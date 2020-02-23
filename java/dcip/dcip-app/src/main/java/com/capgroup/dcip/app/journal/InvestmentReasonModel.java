package com.capgroup.dcip.app.journal;

import com.capgroup.dcip.app.entity.TemporalEntityModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvestmentReasonModel extends TemporalEntityModel {
    private String name;
    private String description;
    private boolean defaultFlag;
    private boolean activeFlag;
}
