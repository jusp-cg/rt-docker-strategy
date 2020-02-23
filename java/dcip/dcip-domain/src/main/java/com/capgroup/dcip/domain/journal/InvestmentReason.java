package com.capgroup.dcip.domain.journal;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "investment_reason_view")

@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_reason_insert(?, ?, ?, ?, ?, ?, ?, ?," +
        " ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_reason_update(?, ?, ?, ?, ?, ?, ?, ?," +
        " ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_reason_delete(?, ?)}")
public class InvestmentReason extends TemporalEntity implements Comparable<InvestmentReason> {
    @Column
    String description;

    @Column
    int orderBy;

    @Column
    private String name;

    @Column
    private boolean defaultFlag = false;

    @Column
    private boolean activeFlag = false;

    @Override
    public int compareTo(InvestmentReason rhs) {
        if (isDefaultFlag()) {
            return rhs.isDefaultFlag() ? Integer.compare(getOrderBy(), rhs.getOrderBy()) : -1;
        }
        if (rhs.isDefaultFlag()) {
            return 1;
        }
        return Integer.compare(getOrderBy(), rhs.getOrderBy());
    }
}
