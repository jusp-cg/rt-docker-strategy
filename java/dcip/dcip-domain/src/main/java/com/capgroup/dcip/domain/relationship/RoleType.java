package com.capgroup.dcip.domain.relationship;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
@Getter
public class RoleType implements Serializable {
    /**
     * Generated Serial Version Id
     */
    private static final long serialVersionUID = 7019918344620795547L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private long id;

    @Column(nullable=false)
    @NotNull
    private String name;

    @Column(nullable = false)
    @NotNull
    private String description;

    public RoleType(String name, String description) {
        this(null, name, description);
    }

    public RoleType(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
