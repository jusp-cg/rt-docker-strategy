package com.capgroup.dcip.domain.canvas;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a canvas on a users workspace
 */
@Table(name = "canvas_view")
@Entity
@NoArgsConstructor
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call canvas_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call canvas_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call canvas_delete(?, ?)}")
public class Canvas extends TemporalEntity {
    /**
     * Generated Serial Version Id
     */
    private static final long serialVersionUID = 3002735519196250646L;

    @OneToMany(mappedBy = "canvas", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Workbench> workbenches;

    @OneToMany(mappedBy = "canvas", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private Set<CanvasItem> canvasItems;

    @Getter
    @Setter
    @Column
    private String name;

    @Setter
    @Getter
    @Column
    private String description;


    @Getter
    @Setter
    @Column
    private Long companyId;

    @Getter
    @Column(insertable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    public Canvas(long id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
        this.canvasItems = new HashSet<>();
        this.workbenches = new HashSet<>();
    }

    public Canvas(String name, String description) {
        this(0, name, description);
    }

    public List<WorkbenchResource> getWorkbenchResources() {
        return workbenches.stream().map(item -> item.getWorkbenchResource()).distinct().collect(Collectors.toList());
    }

    public Iterable<CanvasItem> removeWorkbenchResource(WorkbenchResource resource) {
        if (workbenches.removeIf(x -> x.getWorkbenchResource().equals(resource))) {
            List<CanvasItem> removedCanvasItems =
                    canvasItems.stream().filter(x -> x.getWorkbenchResource().equals(resource)).collect(Collectors.toList());
            canvasItems.removeAll(removedCanvasItems);
            return removedCanvasItems;
        }

        return Collections.emptyList();
    }

    public void addWorkbenchResource(WorkbenchResource resource) {
        workbenches.add(new Workbench(this, resource));
    }

    public CanvasItem addCanvasItem(WorkbenchResource workbenchResource, long entityId) {
        CanvasItem result = new CanvasItem(this, workbenchResource, entityId);
        canvasItems.add(result);
        return result;
    }

    public boolean deleteCanvasItem(WorkbenchResource resource, long entityId) {
        return canvasItems.removeIf(x -> x.getEntityId() == entityId && x.getWorkbenchResource().equals(resource));
    }

    public Iterable<CanvasItem> canvasItemsForWorkbenchResource(WorkbenchResource resource) {
        return canvasItemsForWorkbenchResource(resource.getId());
    }

    public Iterable<CanvasItem> canvasItemsForWorkbenchResource(long resourceId) {
        return canvasItems.stream().filter(x -> x.getWorkbenchResource().getId() == resourceId)
                .collect(Collectors.toList());
    }

}
