package com.capgroup.dcip.domain.canvas;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Models a tool that can be added to a Canvas
 */
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@Getter
public class WorkbenchResource {

    @Getter
    public enum WorkbenchResourceId {
        THESIS(1),
        CHARTS(10),
        NOTE(11),
        CANVASSUMMARY(13),
        MODEL(14);

        private long id;

        WorkbenchResourceId(long id) {
            this.id = id;
        }
    }

    @Id
    @Column(name = "Id")
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "WorkbenchResourceTypeId")
    private WorkbenchResourceType workbenchResourceType;

    @Column(name = "Name")
    private String name;

    @Column(name = "Description")
    private String description;

    @Column(name = "OrderBy")
    private int orderBy;

    @Column(name = "Icon")
    private String icon;

    public WorkbenchResource(WorkbenchResourceType type) {
        workbenchResourceType = type;
    }

    public WorkbenchResourceType getWorkbenchResourceType() {
        return workbenchResourceType;
    }
}
