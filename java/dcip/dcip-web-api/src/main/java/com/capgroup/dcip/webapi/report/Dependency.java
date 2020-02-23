package com.capgroup.dcip.webapi.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dependency {
    private String dependencyName;
    private Long calculationOrder;
    private Long relativeYear;
    private BigDecimal dependencyValue;
}
