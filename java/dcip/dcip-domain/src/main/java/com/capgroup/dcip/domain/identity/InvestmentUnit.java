package com.capgroup.dcip.domain.identity;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "investment_unit_view")
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class InvestmentUnit extends TemporalEntity {
    private String name;
    private String description;
    private String email;
    private boolean test;

    public InvestmentUnit(long id, String name){
        super(id);
        this.name = name;
    }
}
