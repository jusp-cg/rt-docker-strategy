package com.capgroup.dcip.domain.journal;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "investment_type_view")
@NoArgsConstructor
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_type_insert(?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_type_update(?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call investment_type_delete(?, ?)}")
public class InvestmentType extends TemporalEntity implements Comparable<InvestmentType> {
    /**
     * Generated serial version no
     */
    private static final long serialVersionUID = -3454579722948869399L;

    @Column
    String name;

    @Column
    String description;

    @Column
    boolean defaultFlag = false;

    @Column
    boolean activeFlag = false;

    @Column
    int orderBy;

    public InvestmentType(long id, String name, String description) {
        this(id, name, description, false);
    }

    public InvestmentType(long id, String name, String description, boolean isDefault) {
        super(id);
        this.name = name;
        this.description = description;
        this.defaultFlag = isDefault;
    }

    public InvestmentType(String name, String description, boolean isDefault) {
        this(0, name, description, isDefault);
    }

    @Override
    public int compareTo(InvestmentType rhs) {
        if (isDefaultFlag()) {
            return rhs.isDefaultFlag() ? Integer.compare(getOrderBy(), rhs.getOrderBy()) : -1;
        }
        if (rhs.isDefaultFlag()) {
            return 1;
        }
        return Integer.compare(getOrderBy(), rhs.getOrderBy());
    }
}
