package com.capgroup.dcip.domain.canvas;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import com.capgroup.dcip.domain.entity.TemporalEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "canvas_item_view")
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call canvas_item_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call canvas_item_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call canvas_item_delete(?, ?)}")
@NoArgsConstructor
public class CanvasItem extends TemporalEntity {

	private static final long serialVersionUID = -3617963673476505544L;

	@ManyToOne
	@JoinColumn(name="canvas_id")
	private Canvas canvas;

	@ManyToOne(optional = false)
	@JoinColumn(name = "workbench_resource_id")
	@Getter
	private WorkbenchResource workbenchResource;

	@Column
	@Getter
	private long entityId;
	
	public CanvasItem(Canvas canvas, WorkbenchResource workbenchResource, long entityId) {
		this.canvas = canvas;
		this.workbenchResource = workbenchResource;
		this.entityId = entityId;
	}
}