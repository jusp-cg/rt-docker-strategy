package com.capgroup.dcip.app.canvas;

import java.util.List;

import org.mapstruct.Mapper;

import com.capgroup.dcip.domain.canvas.WorkbenchResource;

/**
 * Mapping between a WorkbenchResource and a WorkbenchResourceModel
 */
@Mapper
public interface WorkbenchResourceMapper {
	WorkbenchResource map(WorkbenchResourceModel model);

	WorkbenchResourceModel map(WorkbenchResource resource);
	
	List<WorkbenchResourceModel> mapToModel(Iterable<WorkbenchResource> resources);
}
