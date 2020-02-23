package com.capgroup.dcip.webapi.controllers;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import com.capgroup.dcip.app.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capgroup.dcip.app.canvas_summary.CanvasSummaryModel;
import com.capgroup.dcip.app.canvas_summary.CanvasSummaryService;
import com.capgroup.dcip.app.canvas_summary.CanvasSummaryViewOption;


import com.capgroup.dcip.app.identity.ProfileService;
import com.capgroup.dcip.app.reference.company.CompanySummaryModel;
import com.capgroup.dcip.app.reference.company.CompanySummaryService;
import com.capgroup.dcip.app.thesis.service.ThesisService;
import com.capgroup.dcip.app.thesis.model.ThesisTreeModel;
import com.capgroup.dcip.domain.data.TimeSeries;

import com.capgroup.dcip.webapi.report.CompanySummaryReportGenerator;


/**
 * External API for creating/reading/updating notes 
 */
@RestController
@RequestMapping("api/dcip/canvassummary")
public class CanvasSummaryController {

	private CanvasSummaryService canvasSummaryService;
	private CompanySummaryService companySummaryService;
	private ProfileService profileService;
	private ThesisService thesisService;
	private TimeSeriesService timeSeriesService;

	private CompanySummaryReportGenerator companySummaryReportGenerator;

	@Autowired
	public CanvasSummaryController(CanvasSummaryService canvasSummaryService,
								   CompanySummaryService companySummaryService,
								   ProfileService profileService,
								   ThesisService thesisService,
								   TimeSeriesService timeSeriesService,
								   CompanySummaryReportGenerator companySummaryReportGenerator) {

		this.canvasSummaryService = canvasSummaryService;
		this.companySummaryService = companySummaryService;
		this.profileService = profileService;
		this.thesisService = thesisService;
		this.timeSeriesService = timeSeriesService;
		this.companySummaryReportGenerator = companySummaryReportGenerator;
	}

	@GetMapping
	public Iterable<CanvasSummaryModel> get(@RequestParam(name = "canvasId", required = false) Long canvasId,
											@RequestParam(name = "profileId", required = false) Long profileId,
											@RequestParam(name = "view", required = false) List<CanvasSummaryViewOption> viewOptions) {
		return canvasSummaryService.findAll(canvasId, profileId, toEnumSet(viewOptions));
	}


	@GetMapping("/{canvasSummaryId}")
	public CanvasSummaryModel get(@PathVariable("canvasSummaryId") long canvasSummaryId) {
		return canvasSummaryService.findById(canvasSummaryId);
	}

	@PostMapping
	public CanvasSummaryModel post(@RequestBody CanvasSummaryModel canvasSummaryModel) {
		return canvasSummaryService.create(canvasSummaryModel);
	}

	@PutMapping("/{canvasSummaryId}")
	public CanvasSummaryModel put(@PathVariable("canvasSummaryId") long canvasSummaryId, @RequestBody CanvasSummaryModel canvasSummaryModel) {
		return canvasSummaryService.update(canvasSummaryId, canvasSummaryModel);
	}

	@DeleteMapping("/{canvasSummaryId}")
	public CanvasSummaryModel delete(@PathVariable("canvasSummaryId") long canvasSummaryId) {
		return canvasSummaryService.delete(canvasSummaryId);
	}

	@PostMapping("/{canvasSummaryId}/downloadDocx")
	public void downloadDocx(@PathVariable("canvasSummaryId") long canvasSummaryId,
							 @RequestParam(name = "companyId", required = true) long companyId,
							 @RequestParam(name = "profileId", required = true) long profileId,
							 @RequestParam(name = "canvasId", required = true) long canvasId,
							 @RequestBody List<TimeSeriesQueryModel> request,
							 HttpServletResponse response) throws IOException {

		//Get the ThesisTree
		long thesisId = 0L;
		Iterable<ThesisTreeModel> thesisTreeModel = null;

		if (thesisService.findByCanvasId(canvasId) != null) {
			thesisId = thesisService.findByCanvasId(canvasId).getId();
			thesisTreeModel = thesisService.findThesisTree(thesisId);
		}

		//Get Time Series for Company
		TimeSeriesQueryModel tsQueryModel = new TimeSeriesQueryModel(companyId, 1L, "-10Yrs", null, null);
		TimeSeriesModel<TimeSeries.Entry> compPrice = timeSeriesService.query(tsQueryModel);

		//Get Time Series for Index
		// We need the companyModel to find the Country Location
		Optional<CompanySummaryModel> compSummary = companySummaryService.findSummaryById(companyId);
		long indexSeries = 8L; //default to SP500, else MSCI
		if (compSummary.isPresent()) {
			String countryLoc = compSummary.get().getCountryLoc();
			if (!countryLoc.equals("US-Canada")) {
				indexSeries = 9L;
			}
		}

		TimeSeriesQueryModel indexQueryModel = new TimeSeriesQueryModel(companyId, indexSeries, "-10Yrs", null, null);
		TimeSeriesModel<TimeSeries.Entry> compIndex = timeSeriesService.query(indexQueryModel);

		companySummaryReportGenerator.exportDocxReport(canvasSummaryService.findById(canvasSummaryId),
				compSummary,
				profileService.findById(profileId),
				thesisTreeModel,
				compPrice,
				compIndex,
				timeSeriesService.queryAll(request),
				response);
	}



	protected EnumSet<CanvasSummaryViewOption> toEnumSet(List<CanvasSummaryViewOption> viewOptions){
		return (viewOptions == null || viewOptions.isEmpty()) ? EnumSet.allOf(CanvasSummaryViewOption.class)
				: EnumSet.copyOf(viewOptions);
	}
}
