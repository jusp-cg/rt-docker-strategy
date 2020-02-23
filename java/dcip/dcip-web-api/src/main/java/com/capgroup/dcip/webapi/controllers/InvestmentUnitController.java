package com.capgroup.dcip.webapi.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgroup.dcip.app.identity.InvestmentUnitModel;
import com.capgroup.dcip.app.identity.InvestmentUnitService;

/**
 * REST API for reading investment units (CWI/CRGI/etc.)
 */
@RestController
@RequestMapping("api/dcip/investment-units")
public class InvestmentUnitController {
	private InvestmentUnitService investmentUnitService;

	public InvestmentUnitController(InvestmentUnitService investmentUnitService) {
		this.investmentUnitService = investmentUnitService;
	}

	@GetMapping
	public Iterable<InvestmentUnitModel> get(){
		return investmentUnitService.findAll();
	}	

	@GetMapping("/{id}")
	public InvestmentUnitModel get(@PathVariable("id")long id){
		return investmentUnitService.findById(id);
	}
}
