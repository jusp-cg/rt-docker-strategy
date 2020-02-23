package com.capgroup.dcip.app.canvas_summary;

import java.util.EnumSet;

public interface CanvasSummaryService {
	Iterable<CanvasSummaryModel> findAll(Long canvasId, Long profileId, EnumSet<CanvasSummaryViewOption> option);

	CanvasSummaryModel findById(long id);
	
	CanvasSummaryModel create(CanvasSummaryModel model);
	
	CanvasSummaryModel update(long id, CanvasSummaryModel model);
	
	CanvasSummaryModel delete(long id);
}
