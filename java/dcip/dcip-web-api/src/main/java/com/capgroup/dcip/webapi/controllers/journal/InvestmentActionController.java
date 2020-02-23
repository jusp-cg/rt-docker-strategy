package com.capgroup.dcip.webapi.controllers.journal;

import com.capgroup.dcip.app.journal.InvestmentActionModel;
import com.capgroup.dcip.app.journal.InvestmentActionService;
import com.capgroup.dcip.webapi.report.JournalReportGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * External API for manipulating InvestmentActions (i.e. Journal Entries)
 */
@RestController
@RequestMapping("api/dcip/journal/investment-actions")
public class InvestmentActionController {

    private InvestmentActionService investmentActionService;

    private JournalReportGenerator journalReportGenerator;

    @Autowired
    public InvestmentActionController(InvestmentActionService investmentActionService,
                                      JournalReportGenerator journalReportGenerator) {
        this.investmentActionService = investmentActionService;
        this.journalReportGenerator = journalReportGenerator;
    }

    @GetMapping
    public Iterable<InvestmentActionModel> get(@RequestParam(value = "profileId") Long profileId) {
        return investmentActionService.findAll(profileId);
    }

    @GetMapping("/{id}")
    public InvestmentActionModel get(@PathVariable("id") long id) {
        return investmentActionService.findById(id);
    }

    @PostMapping
    public InvestmentActionModel post(@Valid @RequestBody InvestmentActionModel investmentAction) {
        return investmentActionService.create(investmentAction);
    }

    @PutMapping("/{id}")
    public InvestmentActionModel put(@PathVariable("id") long id,
                                     @Valid @RequestBody InvestmentActionModel investmentActionType) {
        return investmentActionService.update(id, investmentActionType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        investmentActionService.delete(id);
    }

    @GetMapping("/{profileId}/downloadCsv")
    public void downloadCsv(@PathVariable(value = "profileId") Long profileId, HttpServletResponse response)
            throws IOException {
        journalReportGenerator.exportCsvReport(profileId, investmentActionService.findAll(profileId),
                response);
    }

    @GetMapping("/{profileId}/downloadXls")
    public void downloadXls(@PathVariable(value = "profileId") Long profileId, HttpServletResponse response)
            throws IOException {
        journalReportGenerator.exportExcelReport(profileId, investmentActionService.findAll(profileId),
                response);
    }

    @GetMapping("/{id}/same-company")
    public Iterable<InvestmentActionModel> getActionsForSameCompany(@PathVariable("id") long id, @RequestParam(value = "profileId") Long profileId) {
        InvestmentActionModel investmentActionModel = investmentActionService.findById(id);

        return investmentActionService.findByCompanyIdAndProfileId(investmentActionModel.getCompanyId(), profileId);
    }
}
