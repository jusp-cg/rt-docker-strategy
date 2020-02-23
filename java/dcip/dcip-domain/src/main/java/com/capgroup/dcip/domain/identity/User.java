package com.capgroup.dcip.domain.identity;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a user of the system, e.g. JMKB
 */
@Table(name = "User_view")
@javax.persistence.Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLInsert(check = ResultCheckStyle.NONE, callable = true, sql = "{call User_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(check = ResultCheckStyle.NONE, callable = true, sql = "{call User_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(check = ResultCheckStyle.NONE, callable = true, sql = "{call User_delete(?, ?)}")
public class User extends TemporalEntity {
	/**
	 * Generated Serial Version Id
	 */
	private static final long serialVersionUID = 4490146212635431596L;

	@NotNull
	private String initials;

	@NotNull
	private String name;

	@Column
	@NotNull
	@Setter
	private boolean active = true;

	@ManyToOne
	@Getter
	@JoinColumn(name = "investment_unit_id")
	private InvestmentUnit investmentUnit;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<OnBehalfOfUser> onBehalfOfUsers;

	public User(long id, String initials, String name, InvestmentUnit investmentUnit) {
		super(id);
		this.initials = initials;
		this.name = name;
		this.investmentUnit = investmentUnit;
	}

	public User(String initials, String name, InvestmentUnit investmentUnit) {
		this(0, initials, name, investmentUnit);
	}

	public Iterable<User> getOnBehalfOfUsers() {
		return onBehalfOfUsers.stream().map(x->x.getOnBehalfOfUser()).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "User[id:" + getId() + ",initials: + " + getInitials() +  "]";
	}
}
