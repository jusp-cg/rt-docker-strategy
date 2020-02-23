package com.capgroup.dcip.domain.alert;

import com.capgroup.dcip.domain.entity.EntityType;
import com.capgroup.dcip.domain.identity.Identifiable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Meta data for the EntityType (e.g. Canvas/ThesisPoint) that can be associated
 * with an AlertDefinition
 */
@Entity
@EqualsAndHashCode(of="id")
public class AlertDefinitionAssociationType implements Identifiable {
	@Getter
	@Id
	@Column
	private Long id;

	@ManyToOne
	@JoinColumn(name = "entity_type_id", nullable = false)
	@Getter
	private EntityType entityType;
}
