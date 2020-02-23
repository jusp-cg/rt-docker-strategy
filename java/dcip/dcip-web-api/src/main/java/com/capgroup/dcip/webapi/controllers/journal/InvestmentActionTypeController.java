package com.capgroup.dcip.webapi.controllers.journal;

import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgroup.dcip.app.journal.InvestmentActionTypeModel;
import com.capgroup.dcip.app.journal.InvestmentActionTypeService;

@RestController
@RequestMapping("api/dcip/journal/investment-action-types")
public class InvestmentActionTypeController {

    private InvestmentActionTypeService investmentActionTypeService;

    @Autowired
    public InvestmentActionTypeController(InvestmentActionTypeService investmentActionTypeService) {
        this.investmentActionTypeService = investmentActionTypeService;
    }

    @GetMapping
    public Stream<InvestmentActionTypeModel> get() {
        return investmentActionTypeService.findAll();
    }

    @GetMapping("/{id}")
    public InvestmentActionTypeModel get(@PathVariable("id") long id) {
        return investmentActionTypeService.findById(id);
    }

    @PostMapping
    public InvestmentActionTypeModel post(@Valid @RequestBody InvestmentActionTypeModel investmentActionType) {
        return investmentActionTypeService.create(investmentActionType);
    }

    @PutMapping("/{id}")
    public InvestmentActionTypeModel put(@PathVariable("id") long id,
                                         @Valid @RequestBody InvestmentActionTypeModel investmentActionType) {
        return investmentActionTypeService.update(id, investmentActionType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        investmentActionTypeService.delete(id);
    }
}
