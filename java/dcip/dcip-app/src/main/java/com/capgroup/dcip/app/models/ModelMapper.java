package com.capgroup.dcip.app.models;

import com.capgroup.dcip.app.canvas.CanvasModel;
import com.capgroup.dcip.app.canvas.CanvasService;
import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.canvas.WorkbenchResource;
import com.capgroup.dcip.domain.models.Model;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(config = TemporalEntityMapper.class)
public abstract class ModelMapper {

    @Autowired
    private CanvasService canvasService;

    @InheritConfiguration(name = "map")
    abstract void updateModel(ModelDetails details, @MappingTarget Model entity);

    public ModelDetails map(Model entity, Long canvasId) {
        ModelDetails result = doMap(entity);
        result.setCanvasId(canvasId);
        return result;
    }

    public ModelDetails map(Model entity) {
        ModelDetails result = doMap(entity);
        canvasService.findByCanvasItemEntityId(WorkbenchResource.WorkbenchResourceId.MODEL, entity.getId())
                .ifPresent(canvasModel->result.setCanvasId(canvasModel.getId()));
        return result;
    }

    abstract protected ModelDetails doMap(Model entity);

    abstract public Model map(ModelDetails details);
}
