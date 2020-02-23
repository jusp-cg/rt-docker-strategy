package com.capgroup.dcip.webapi.controllers;

import com.capgroup.dcip.domain.data.SeriesFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capgroup.dcip.app.data.SeriesModel;
import com.capgroup.dcip.app.data.SeriesService;

import java.util.EnumSet;

/**
 * External API for retrieving Series
 */
@RestController
@RequestMapping("api/dcip/series")
public class SeriesController {

	private SeriesService seriesService;

	@Autowired
	public SeriesController(SeriesService seriesService) {
		this.seriesService = seriesService;
	}
	
	@GetMapping
	public Iterable<SeriesModel> get(@RequestParam(value = "filter", required = false) EnumSet<SeriesFilter> filters) {
		return seriesService.findAll(filters==null || filters.isEmpty() ? EnumSet.allOf(SeriesFilter.class) : filters);
	}

	@GetMapping("/{id}")
	public SeriesModel get(@PathVariable("id") long id) {
		return seriesService.findByIdOrElseThrow(id);
	}

}
