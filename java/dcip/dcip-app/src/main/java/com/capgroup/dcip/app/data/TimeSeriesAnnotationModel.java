package com.capgroup.dcip.app.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.capgroup.dcip.app.entity.TemporalEntityModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object that represents a TimeSeries Annotation
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TimeSeriesAnnotationModel extends TemporalEntityModel {
	private LocalDateTime timeStamp;
	private String text;
	private long entityId;
	private BigDecimal value;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
}
