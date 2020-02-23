package com.capgroup.dcip.domain.thesis;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import com.capgroup.dcip.domain.entity.TemporalEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "thesis_point_view")
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call thesis_point_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call thesis_point_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call thesis_point_delete(?, ?)}")
@Getter
@Setter
@NoArgsConstructor
public class ThesisPoint extends TemporalEntity {

	private static final long serialVersionUID = -3914093253866210539L;

	@Column
	private String text;

	@Column
	private long originalThesisId;
	
	public ThesisPoint(String text, long originalThesisId) {
		this.text = text;
		this.originalThesisId = originalThesisId;
	}
	
	public static boolean equals(ThesisPoint lhs, ThesisPoint rhs) {
		return Objects.equals(lhs, rhs);
	}
}
