package com.capgroup.dcip.domain.identity;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLInsert;

import javax.persistence.*;

/**
 * A profile represents a User in a specific Role (e.g. STMF as an Analyst)
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Table(name = "profile_view")
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call profile_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
public class Profile extends TemporalEntity {
    /**
     * Generated Serial Version Id
     */
    private static final long serialVersionUID = 6794214276491665629L;

    @Column
    boolean defaultFlag;

    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    private User user;

    @JoinColumn(name = "role_id")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Role role;

    @JoinColumn(name = "application_role_id")
    @ManyToOne(optional = false)
    private ApplicationRole applicationRole;

    @Column(name="application_role_id", updatable = false, insertable = false)
    private long applicationRoleId;

    public Profile(User user, Role role) {
        this(0, user, role);
    }

    public Profile(long id, User user, Role role) {
        super(id);
        this.user = user;
        this.role = role;
    }
}
