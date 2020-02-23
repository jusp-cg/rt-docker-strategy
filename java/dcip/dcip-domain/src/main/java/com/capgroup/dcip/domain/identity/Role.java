package com.capgroup.dcip.domain.identity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLInsert;

import com.capgroup.dcip.domain.entity.TemporalEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents role, e.g. Analyst or PM
 */
@Table(name="role_view")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call role_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
public class Role extends TemporalEntity {
	/**
	 * Generated Serial Version Id
	 */
	private static final long serialVersionUID = -8989119462640111413L;

	@Column
	@NotNull
	private String name;

	@Column
	@NotNull
	private String description;

	public Role(long id, String name, String description) {
		super(id);
		this.name = name;
		this.description = description;
	}
	
	public Role(String name, String description) {
		this(0, name, description);
	}
}
