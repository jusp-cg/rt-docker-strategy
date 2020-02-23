package com.capgroup.dcip.app.canvas;

import javax.validation.constraints.NotNull;

import com.capgroup.dcip.app.entity.TemporalEntityModel;
import com.capgroup.dcip.app.reference.company.CompanyModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data transfer object that represents a Canvas
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CanvasModel extends TemporalEntityModel {
	@NotNull
	private String name;
	
	@NotNull
	private String description;
	
	private CompanyModel company;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime createdTimestamp;
}
