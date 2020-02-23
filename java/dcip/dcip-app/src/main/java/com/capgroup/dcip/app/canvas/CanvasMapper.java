package com.capgroup.dcip.app.canvas;

import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.app.reference.company.CompanyService;
import com.capgroup.dcip.domain.canvas.Canvas;
import com.capgroup.dcip.domain.canvas.CanvasItem;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * responsible for mapping between a Canvas (domain object) and CanvasModel
 * (DTO), and vice-versa
 */
@Slf4j
@Mapper(config = TemporalEntityMapper.class, uses = WorkbenchResourceMapper.class)
public abstract class CanvasMapper {

    @Autowired
    CompanyService companyService;

    /**
     * Mapping between a Canvas (domain object) and CanvasModel (DTO)
     */
    public abstract CanvasModel map(Canvas canvas);

    /**
     * Mapping between a CanvasItem and CanvasItemModel
     */
    public abstract CanvasItemModel map(CanvasItem canvasItem);

    /**
     * Mapping between a collection of Canvases and a collection of CanvasModels.
     * The mapping rules for the fields are defined in toCanvasModel
     */
    public Iterable<CanvasModel> mapToCanvasModels(Iterable<Canvas> canvases) {
        long start = 0;
        if (log.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }

        Iterable<CanvasModel> results = doMapping(canvases);

        if (log.isDebugEnabled()) {
            log.debug("Time to mapEntity domain Canvases to DTO took {}ms", System.currentTimeMillis() - start);
        }

        return results;
    }

    protected abstract Iterable<CanvasModel> doMapping(Iterable<Canvas> canvases);

    /**
     * Mapping between a collection of CanvaseItems and a collection of
     * CanvasItemModels. The mapping rules for the fields are defined in
     * mapEntity(CanvasItemModel)
     */
    public abstract Iterable<CanvasItemModel> mapToCanvasItemModels(Iterable<CanvasItem> canvasItemModels);

    /**
     * Mapping between a CanvasModel and a Canvas
     */
    @Mapping(target = "companyId", expression = "java(model.getCompany() == null ? null : model.getCompany().getId())")
    public abstract Canvas map(CanvasModel model);

    /**
     * Updates an existing Canvas with the data from a CanvasModel
     */
    @InheritConfiguration(name = "map")
    public abstract void updateCanvas(CanvasModel model, @MappingTarget Canvas canvas);

    /**
     * post processing after mapping to a canvas model
     */
    @AfterMapping
    void afterMapping(Canvas canvas, @MappingTarget CanvasModel canvasModel) {
        if (canvas.getCompanyId() != null) {
            canvasModel.setCompany(CompanyService.findByIdOrUnknown(companyService, canvas.getCompanyId()));
        }
    }
}
