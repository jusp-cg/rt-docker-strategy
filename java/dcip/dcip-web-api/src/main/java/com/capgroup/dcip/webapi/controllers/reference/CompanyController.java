package com.capgroup.dcip.webapi.controllers.reference;

import com.capgroup.dcip.app.reference.company.*;
import com.capgroup.dcip.domain.reference.company.SymbolType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;

/**
 * External API for accessing Company information
 */
@RestController
@RequestMapping("api/dcip/reference/companies")
public class CompanyController {
    private CompanyService companyService;
    private CompanySummaryService companySummaryService;

    @Autowired
    public CompanyController(CompanyService companyService, CompanySummaryService companySummaryService) {
        this.companyService = companyService;
        this.companySummaryService = companySummaryService;
    }

    @GetMapping
    public Iterable<CompanyModel> get(@RequestParam(name = "matches", required = false) String matches,
                                      @RequestParam(name = "companyType", required = false) List<CompanyType> companyTypes,
                                      @RequestParam(name = "tickerSymbol", required = false) String tickerSymbol,
                                      @RequestParam(name = "exchangeSymbol", required = false) String exchangeSymbol,
                                      @RequestParam(name = "symbol", required = false) String symbol,
                                      @RequestParam(name = "symbolType", required = false) SymbolType symbolType) {
        return matches != null ? companyService.findAll(matches,
                (companyTypes == null || companyTypes.isEmpty()) ? EnumSet.allOf(CompanyType.class)
                        : EnumSet.copyOf(companyTypes))
                : companyService.findAll(tickerSymbol, exchangeSymbol, symbol, symbolType);
    }

    @GetMapping("/{id}")
    public CompanyModel get(@PathVariable("id") long id) {
        return CompanyService.findByIdOrUnknown(companyService, id);
    }

    @GetMapping("/summary/{id}")
    public CompanySummaryModel getSummary(@PathVariable("id") long id) {
        return CompanySummaryService.findByIdOrUnknown(companySummaryService, id);
    }


}
