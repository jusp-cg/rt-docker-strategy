package com.capgroup.dcip.domain.canvas;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import com.capgroup.dcip.domain.entity.TemporalEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a workbench resource when it is added to a canvas
 */
@Table(name = "workbench_view")
@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call workbench_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call workbench_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call workbench_delete(?, ?)}")
public class Workbench extends TemporalEntity {

	private static final long serialVersionUID = -8554844308804270314L;

	@JoinColumn(name = "canvas_id")
	@ManyToOne(optional = false)
	private Canvas canvas;

	@JoinColumn(name = "workbench_resource_id")
	@ManyToOne(optional = false)
	private WorkbenchResource workbenchResource;

	public Workbench(Canvas canvas, WorkbenchResource resource) {
		this.canvas = canvas;
		this.workbenchResource = resource;
	}
}
