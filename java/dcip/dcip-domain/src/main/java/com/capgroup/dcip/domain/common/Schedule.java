package com.capgroup.dcip.domain.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
public class Schedule {
    private String cron;
    private Long fixedDelay;
    private Long fixedRate;
}
