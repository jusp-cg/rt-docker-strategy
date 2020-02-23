package com.capgroup.dcip.app.canvas;

import java.util.Comparator;
import java.util.List;

import com.capgroup.dcip.domain.canvas.WorkbenchResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.domain.canvas.Canvas;
import com.capgroup.dcip.infrastructure.repository.CanvasRepository;
import com.capgroup.dcip.infrastructure.repository.WorkbenchResourceRepository;

@Service
public class WorkbenchResourceServiceImpl implements WorkbenchResourceService {

	private WorkbenchResourceRepository workbenchResourceRepository;
	private CanvasRepository canvasRepository;
	private WorkbenchResourceMapper workbenchResourceMapper;

	@Autowired
	public WorkbenchResourceServiceImpl(WorkbenchResourceRepository repository,
			CanvasRepository canvasRepository,
			WorkbenchResourceMapper workbenchResourceMapper) {
		this.workbenchResourceRepository = repository;
		this.canvasRepository = canvasRepository;
		this.workbenchResourceMapper = workbenchResourceMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public List<WorkbenchResourceModel> findAll() {
		return workbenchResourceMapper.mapToModel(workbenchResourceRepository.findAll());
	}

	@Override
	@Transactional(readOnly = true)
	public List<WorkbenchResourceModel> findforCanvas(long canvasId) {
		Canvas canvas = canvasRepository.findById(canvasId)
				.orElseThrow(() -> new ResourceNotFoundException("Canvas", "canvasId"));
		List<WorkbenchResource> workbenchResourceList = canvas.getWorkbenchResources();
		workbenchResourceList.sort(Comparator.comparing(WorkbenchResource::getOrderBy));
		return workbenchResourceMapper.mapToModel(workbenchResourceList);
	}
}
