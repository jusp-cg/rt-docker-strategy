package com.capgroup.dcip.app.reference.company;

import com.capgroup.dcip.domain.identity.Identifiable;
import lombok.Data;

@Data
public class TradingitemModel implements Identifiable<Long> {
    Long id;
    boolean primary;
}
