package com.capgroup.dcip.webapi.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Calculation {
    private String value;

    private String fieldName;
    private List<Dependency> dependencys;
    private String formula;
}
