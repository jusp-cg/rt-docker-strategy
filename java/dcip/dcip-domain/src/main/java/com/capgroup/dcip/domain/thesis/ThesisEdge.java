package com.capgroup.dcip.domain.thesis;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.*;
import java.util.EnumSet;
import java.util.Objects;

@Entity
@Table(name = "thesis_edge_view")
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call thesis_edge_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
        " ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call thesis_edge_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
        " ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call thesis_edge_delete(?, ?)}")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThesisEdge extends TemporalEntity implements Comparable<ThesisEdge> {

    private static final long serialVersionUID = -6302330089751790724L;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "thesis_id")
    private Thesis thesis;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "child_thesis_point_id")
    private ThesisPoint childThesisPoint;

    @ManyToOne(optional = true, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "parent_thesis_point_id")
    private ThesisPoint parentThesisPoint;

    @Column
    private int orderBy;

    @Override
    public int compareTo(ThesisEdge o) {
        return Integer.compare(orderBy, o.orderBy);
    }

    public boolean matches(ThesisPoint thesisPoint, EnumSet<Thesis.ThesisFilter> filter) {
        return (filter.contains(Thesis.ThesisFilter.PARENT) && Objects.equals(getParentThesisPoint(), thesisPoint))
                || (filter.contains(Thesis.ThesisFilter.CHILD) && Objects.equals(getChildThesisPoint(), thesisPoint));

    }

    public boolean matches(Long thesisPointId, EnumSet<Thesis.ThesisFilter> filter) {
        return (filter.contains(Thesis.ThesisFilter.PARENT) && Objects.equals(getParentThesisPoint() == null ? null :
                getParentThesisPoint().getId(), thesisPointId))
                || (filter.contains(Thesis.ThesisFilter.CHILD) && Objects.equals(getChildThesisPoint() == null ?
                null : getChildThesisPoint().getId(), thesisPointId));
    }
}
