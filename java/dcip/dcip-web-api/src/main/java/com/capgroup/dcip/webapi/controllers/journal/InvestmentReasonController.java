package com.capgroup.dcip.webapi.controllers.journal;

import com.capgroup.dcip.app.journal.InvestmentReasonModel;
import com.capgroup.dcip.app.journal.InvestmentReasonService;
import com.capgroup.dcip.app.journal.InvestmentTypeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/dcip/journal/investment-reasons")
public class InvestmentReasonController {
    InvestmentReasonService investmentReasonService;

    @Autowired
    public InvestmentReasonController(InvestmentReasonService investmentReasonService) {
        this.investmentReasonService = investmentReasonService;
    }

    @GetMapping
    public Iterable<InvestmentReasonModel> get(@RequestParam(value="userInitials", required = false)
                                               String userInitials,
                                               @RequestParam(value="includeDefaults", required = false) Boolean includeDefaults) {
        return investmentReasonService.findByUserInitials(userInitials, includeDefaults);
    }

    @PostMapping
    public InvestmentReasonModel post(@Valid @RequestBody InvestmentReasonModel investmentReason) {
        return investmentReasonService.create(investmentReason);
    }

    @PutMapping("/{id}")
    public InvestmentReasonModel put(@PathVariable("id") long id,
                                   @Valid @RequestBody InvestmentReasonModel investmentReason) {
        return investmentReasonService.update(id, investmentReason);
    }
}
