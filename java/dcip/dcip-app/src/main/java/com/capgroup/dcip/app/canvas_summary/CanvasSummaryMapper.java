package com.capgroup.dcip.app.canvas_summary;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.jsoup.Jsoup;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.capgroup.dcip.app.canvas.CanvasService;
import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.canvas.WorkbenchResource.WorkbenchResourceId;
import com.capgroup.dcip.domain.canvas_summary.CanvasSummary;

/**
 * Mapping between the domain entity CanvasSummary and DTO CanvasSummaryModel
 */
@Mapper(config = TemporalEntityMapper.class)
public abstract class CanvasSummaryMapper {


	@Autowired
	private CanvasService canvasService;

	/**
	 * Updates a CanvasSummary from a CanvasSummaryModel
	 */
	@InheritConfiguration(name = "map")
	public abstract CanvasSummary update(CanvasSummaryModel model, @MappingTarget CanvasSummary canvasSummary);

	/**
	 * Converts a CanvasSummaryModel to a CanvasSummary
	 */
	public abstract CanvasSummary map(CanvasSummaryModel canvasSummary);

	/**
	 * Converts a collection of CanvasSummarys to a collection of CanvasSummaryModels using the
	 * provided canvasId
	 */
	public Iterable<CanvasSummaryModel> map(Iterable<CanvasSummary> canvasSummarys, Long canvasId) {
		return canvasId == null ? map(canvasSummarys) : StreamSupport.stream(doMap(canvasSummarys).spliterator(), false).map(x -> {
			x.setCanvasId(canvasId);
			return x;
		}).collect(Collectors.toList());
	}

	/**
	 * Converts a Collection of CanvasSummarys to CanvasSummaryModels - the canvas Id is retrieved from
	 * the DB for each CanvasSummary
	 */
	public Iterable<CanvasSummaryModel> map(Iterable<CanvasSummary> canvasSummarys) {
		return StreamSupport.stream(doMap(canvasSummarys).spliterator(), false).map(x -> {
			canvasService.findByCanvasItemEntityId(WorkbenchResourceId.NOTE, x.getId()).ifPresent(canvas -> {
				x.setCanvasId(canvas.getId());
			});
			return x;
		}).collect(Collectors.toList());
	}
	
	/**
	 * Converts a CanvasSummary to a CanvasSummaryModel, retrieving the canvasId from the canvas
	 * service
	 */
	public CanvasSummaryModel map(CanvasSummary canvasSummary) {
		CanvasSummaryModel result = doMap(canvasSummary);
		canvasService.findByCanvasItemEntityId(WorkbenchResourceId.NOTE, canvasSummary.getId()).ifPresent(canvas -> {
			result.setCanvasId(canvas.getId());
		});
		return result;
	}

	public CanvasSummaryModel map(CanvasSummary canvasSummary, long canvasId) {
		CanvasSummaryModel result = map(canvasSummary);
		result.setCanvasId(canvasId);
		return result;
	}

	protected Iterable<CanvasSummaryModel> doMap(Iterable<CanvasSummary> canvasSummarys) {
		return StreamSupport.stream(canvasSummarys.spliterator(), false).map(this::doMap).collect(Collectors.toList());
	}

	protected abstract CanvasSummaryModel doMap(CanvasSummary canvasSummary);

//	@AfterMapping
//	protected void afterMapping(@MappingTarget CanvasSummary canvasSummary) {
//		String summary = Jsoup.parseBodyFragment(canvasSummary.getDetail()).text();
//		if (summary.length() > 0)
//			canvasSummary.setSummary(summary.substring(0, Integer.min(SUMMARY_TEXT_LENGTH, summary.length())));
//	}
}
