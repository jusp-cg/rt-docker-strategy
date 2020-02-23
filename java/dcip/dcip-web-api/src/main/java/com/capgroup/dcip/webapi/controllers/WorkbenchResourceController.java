package com.capgroup.dcip.webapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgroup.dcip.app.canvas.WorkbenchResourceModel;
import com.capgroup.dcip.app.canvas.WorkbenchResourceService;

@RestController
@RequestMapping("api/dcip/workbench-resources")
public class WorkbenchResourceController {
	WorkbenchResourceService workbenchResourceService;
	
	@Autowired
	public WorkbenchResourceController(WorkbenchResourceService workbenchResourceService) {
		this.workbenchResourceService = workbenchResourceService;
	}
	
	@GetMapping
	public List<WorkbenchResourceModel> get(){
		return workbenchResourceService.findAll();
	}
}
