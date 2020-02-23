package com.capgroup.dcip.domain.identity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;

/**
 * Represents an application role of a profile which controls application security
 */
@ToString
@Getter
@EqualsAndHashCode
@Entity
public class ApplicationRole implements Identifiable<Long> {
    @Column(name = "Id")
    @Id
    private Long id;
    @Column(name = "Name")
    private String name;
    @Column(name = "Description")
    private String description;

    public enum ApplicationRoleId {
        Administrator(1),
        TestRole(2),
        BusinessRole(3),
        ExternalApplication(4);

        @Getter
        long id;

        ApplicationRoleId(long id) {
            this.id = id;
        }

        public static ApplicationRoleId valueOf(long id) {
            return Arrays.stream(ApplicationRoleId.values()).filter(x -> x.id == id).findFirst().get();
        }
    }
}
