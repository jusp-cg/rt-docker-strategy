package com.capgroup.dcip.webapi.controllers.journal;

import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.capgroup.dcip.app.journal.InvestmentTypeModel;
import com.capgroup.dcip.app.journal.InvestmentTypeService;

/**
 * REST API for creating/updating/deleting investment types
 */
@RestController
@RequestMapping("api/dcip/journal/investment-types")
public class InvestmentTypeController {
	InvestmentTypeService investmentTypeService;

	@Autowired
	public InvestmentTypeController(InvestmentTypeService investmentTypeService) {
		this.investmentTypeService = investmentTypeService;
	}

	@GetMapping
	public Iterable<InvestmentTypeModel> get(@RequestParam(value = "userInitials", required = false) String userInitials,
			@RequestParam(value = "includeDefaults", required = false) Boolean includeDefaults) {
		return investmentTypeService.findByUserInitials(userInitials, includeDefaults);
	}

	@PostMapping
	public InvestmentTypeModel post(@Valid @RequestBody InvestmentTypeModel investmentType) {
		return investmentTypeService.create(investmentType);
	}

	@PutMapping("/{id}")
	public InvestmentTypeModel put(@PathVariable("id") long id,
			@Valid @RequestBody InvestmentTypeModel investmentType) {
		return investmentTypeService.update(id, investmentType);
	}

	@DeleteMapping("/{id}")
	public InvestmentTypeModel delete(@PathVariable("id") long id) {
		return investmentTypeService.delete(id);
	}
}
