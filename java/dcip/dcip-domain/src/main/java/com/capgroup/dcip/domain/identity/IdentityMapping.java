package com.capgroup.dcip.domain.identity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;

@Entity
@Getter
public class IdentityMapping implements Identifiable {
	@Id
	@Column(name = "Id")
	private Long id;

	@Column(name = "DataSourceId")
	private long dataSourceId;

	@Column(name = "EntityTypeId")
	private long entityTypeId;

	@Column(name = "InternalId")
	private long internalId;

	@Column(name = "ExternalId")
	private String externalId;
}