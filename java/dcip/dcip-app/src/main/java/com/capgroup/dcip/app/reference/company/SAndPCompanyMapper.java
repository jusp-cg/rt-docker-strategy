package com.capgroup.dcip.app.reference.company;

import com.capgroup.dcip.app.identity.IdentityMappingService;
import com.capgroup.dcip.sand.company.Company;
import com.capgroup.dcip.sand.company.CompanySummary;
import com.capgroup.dcip.sand.company.Security;
import com.capgroup.dcip.sand.company.TradingItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class SAndPCompanyMapper {

//    private static final long CUSIP_SYMBOL_TYPE_ID = 15;
//    private static final long INTERNATIONAL_SEDOL_SYMBOL_TYPE_ID = 22;
//    private static final long DOMESTIC_SEDOL_SYMBOL_TYPE_ID = 5701;

    @Autowired
    private IdentityMappingService identityMappingService;

    @Mapping(target = "companyType", ignore = true)
    public abstract CompanyModel map(Company company);

    public abstract CompanySummaryModel map(CompanySummary companySummary);

    //  public abstract SecurityModel map(Security company);

    //  public abstract Iterable<SecurityModel> mapSecurities(Iterable<Security> securities);

    public abstract Iterable<CompanyModel> mapCompanies(Iterable<Company> companies);

    @AfterMapping
    protected void afterMap(Company src, @MappingTarget CompanyModel destination) {
        // set the CG specific id
        identityMappingService.internalIdentifier(Long.toString(src.getId()), SAndPConstants.SANDP_DATASOURCE_ID,
                SAndPConstants.COMPANY_TYPE_ID).ifPresent(destination::setId);

        // set the short name - use the symble ticker - if not use the long name
        destination.setShortName(src.getSecurities().stream().filter(Security::isPrimaryFlag).findFirst()
                .map(x -> x.getTradingItems().stream().filter(TradingItem::isPrimaryFlag).findFirst()
                        .map(TradingItem::getSymbolTicker).orElse(src.getName()))
                .orElse(src.getName()));

        destination.setCompanyType(CompanyType.fromValue(src.getCompanyType()).get());
    }

    @AfterMapping
    protected void afterMap(CompanySummary src, @MappingTarget CompanySummaryModel destination) {
        // set the CG specific id to replace sandp id
        identityMappingService.internalIdentifier(Long.toString(src.getCompanyId()), SAndPConstants.SANDP_DATASOURCE_ID,
                SAndPConstants.COMPANY_TYPE_ID).ifPresent(destination::setCompanyId);

    }
    /*
    @AfterMapping
    protected void afterMap(Security src, @MappingTarget SecurityModel destination) {

        // set the symbols and the ticker (i.e. data from the trading item and symbol)
        src.getTradingItems().stream().filter(TradingItem::isPrimaryFlag).findFirst().ifPresent(tradingItem -> {
            destination.setTicker(tradingItem.getSymbolTicker());

            // set the internal id
            identityMappingService.internalIdentifier(Long.toString(tradingItem.getId()),
            		SAndPConstants.SANDP_DATASOURCE_ID, SAndPConstants.SECURITY_TYPE_ID).ifPresent(destination::setId);

            // set the symbols
            if (tradingItem.getSymbols() != null) {
                SymbolsModel symbols = new SymbolsModel();
                destination.setSymbols(symbols);

                mapSymbol(tradingItem, CUSIP_SYMBOL_TYPE_ID, symbols::setCusip);
                mapSymbol(tradingItem, INTERNATIONAL_SEDOL_SYMBOL_TYPE_ID, symbols::setSedol);
                mapSymbol(tradingItem, DOMESTIC_SEDOL_SYMBOL_TYPE_ID, symbols::setSedol);
            }
        });
    }

    protected void mapSymbol(TradingItem tradingItem, long typeId, Consumer<String> consumer) {
        tradingItem.getSymbols().stream().filter(y -> y.getSymbolTypeId() == typeId).map(Symbol::getSymbolValue)
                .findFirst().ifPresent(consumer);
    }*/
}
