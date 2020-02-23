package com.capgroup.dcip.domain.models;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "model_view")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call model_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call model_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call model_delete(?, ?)}")
public class Model extends TemporalEntity {
    private String description;
    private String name;

    public String getKey() {
        return "models/" + getId();
    }
}
