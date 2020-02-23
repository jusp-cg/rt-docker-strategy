package com.capgroup.dcip.app.canvas;

import java.util.List;

public interface WorkbenchResourceService {
	List<WorkbenchResourceModel> findAll();
	
	List<WorkbenchResourceModel> findforCanvas(long canvasId);
}
