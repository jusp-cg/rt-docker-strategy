package com.capgroup.dcip.domain.journal;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
public class InvestmentActionTypeGroup {
    @Column(name = "Id") @Id
    private int id;

    @Column(name = "Name")
    private String name;

    @Column(name = "OrderBy")
    private int orderBy;

}
