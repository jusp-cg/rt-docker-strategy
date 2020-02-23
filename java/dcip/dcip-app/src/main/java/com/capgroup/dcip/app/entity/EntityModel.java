package com.capgroup.dcip.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data transfer object that represents a Entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class EntityModel {
	private long profileId;
	private Long Id;
	private long entityTypeId;
	private String initials;
	private String role;
	private LocalDateTime modifiedTimestamp;
	private LocalDateTime createdTimestamp;
	private long versionNo;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	public LocalDateTime getModifiedTimestamp() {
		return modifiedTimestamp;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	public LocalDateTime getCreatedTimestamp() {
		return createdTimestamp;
	}

}
