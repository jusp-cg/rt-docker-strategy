package com.capgroup.dcip.domain.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * The meta-data for an Entity. 
 *
 */
@javax.persistence.Entity
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
@Getter
public class EntityType implements Serializable {
	/**
	 * Generated Serial Version Id
	 */
	private static final long serialVersionUID = -1586302587001631891L;

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long id;
	
	@Column(nullable=false)
	@NotNull
	private String name;
	
	@Column(nullable = false)
	@NotNull
	private String description;

	@Column
	private String entityUrlTemplate;

	public EntityType(long id){
		this(id, null, null);
	}

	public EntityType(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public EntityType(long id, String name, String description) {
		this(name, description);
		this.id = id;
	}
}
