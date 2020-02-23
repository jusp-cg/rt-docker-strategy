package com.capgroup.dcip.app.reference.capital_system;

import com.capgroup.dcip.cmps.model.Account;
import com.capgroup.dcip.cmps.model.Portfolio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.stream.Stream;

/**
 * Defines the mapping between the CMPS representation of an Account the app
 * representation
 */
@Mapper
public interface CmpsAccountMapper {
    @Mappings({@Mapping(target = "id", source = "portfolioUid"), @Mapping(target = "name", source = "legalName")})
    AccountModel toAccountModel(Account account);

    @Mapping(target = "id", source = "portfolioUid")
    @Mapping(target = "name", source = "investmentPortfolioName",
            defaultExpression = "java(portfolio.getName())")
    AccountModel toAccountModel(Portfolio portfolio);

    Stream<AccountModel> toAccountModelsFromPortfolio(Stream<Portfolio> toAccountModels);
}
