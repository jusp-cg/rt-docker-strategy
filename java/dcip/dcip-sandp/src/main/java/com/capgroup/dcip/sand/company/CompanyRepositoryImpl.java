package com.capgroup.dcip.sand.company;

import com.capgroup.dcip.domain.reference.company.SymbolType;
import com.capgroup.dcip.util.data.DataSourceManager;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * Implementation for accessing Company from the SAndP database
 */
@Component
public class CompanyRepositoryImpl implements CompanyRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyRepositoryImpl.class);
    private static String searchCompanyQuery = "dcipCompanySearch";
    private static String findCompanyByIdQuery = "SELECT company.companyid, " + "       companyname, "
            + "       webpage, " + "       [security].securityid, " + "       [security].securityname, "
            + "       [security].primaryflag  AS securityPrimaryFlag, " + "       tickersymbol, "
            + "       tradingItem.tradingitemid, " + "       tradingItem.exchangeid, "
            + "       tradingItem.currencyid, " + "       tradingItem.tradingitemstatusid, "
            + "       tradingItem.primaryflag AS tradingItemPrimaryFlag, "
            + "       CASE WHEN companyTypeId = 4 AND companyStatusTypeId = 1 THEN 1"
            + "       WHEN security.securityId IS NOT NULL AND companyTypeId != 4 and companyStatusTypeId != 1 THEN 2" +
            " ELSE 4 END AS CompanyType "
            + "       FROM   [xf_target].[dbo].[ciqcompany] company "
            + "       LEFT JOIN [xf_target].[dbo].[ciqsecurity] security "
            + "         ON company.companyid = security.companyid AND security.primaryFlag = 1"
            + "       LEFT JOIN [xf_target].[dbo].[ciqsecuritysubtype] securitySubType "
            + "         ON security.securitysubtypeid = securitySubType.securitysubtypeid "
            + "       LEFT JOIN [xf_target].[dbo].[ciqsecuritytype] securityType "
            + "         ON securitySubType.securitytypeid = securityType.securitytypeid "
            + "       LEFT JOIN [xf_target].[dbo].[ciqtradingitem] tradingItem "
            + "         ON security.securityid = tradingItem.securityid AND tradingItem.primaryFlag = 1"
            + "       WHERE company.companyId = ? ";
    private static String findCompanyByTickerQuery = "SELECT DISTINCT company.companyid, " + "       companyname, "
            + "       webpage, " + "       [security].securityid, " + "       [security].securityname, "
            + "       [security].primaryflag  AS securityPrimaryFlag, " + "       tickersymbol, "
            + "       tradingItem.tradingitemid, " + "       tradingItem.exchangeid, "
            + "       tradingItem.currencyid, " + "       tradingItem.tradingitemstatusid, "
            + "       tradingItem.primaryflag AS tradingItemPrimaryFlag, "
            + "       CASE WHEN companyTypeId = 4 AND companyStatusTypeId = 1 THEN 1"
            + "       WHEN security.securityId IS NOT NULL AND companyTypeId != 4 and companyStatusTypeId != 1 THEN 2" +
            " ELSE 4 END AS CompanyType "
            + "       FROM   [xf_target].[dbo].[ciqcompany] company "
            + "       JOIN [xf_target].[dbo].[ciqsecurity] security "
            + "         ON company.companyid = security.companyid AND security.primaryFlag = 1"
            + "       JOIN [xf_target].[dbo].[ciqsecuritysubtype] securitySubType "
            + "         ON security.securitysubtypeid = securitySubType.securitysubtypeid "
            + "       JOIN [xf_target].[dbo].[ciqsecuritytype] securityType "
            + "         ON securitySubType.securitytypeid = securityType.securitytypeid "
            + "       JOIN [xf_target].[dbo].[ciqtradingitem] tradingItem "
            + "         ON security.securityid = tradingItem.securityid AND tradingItem.primaryFlag = 1"
            + "       JOIN [xf_target].[dbo].[ciqExchange] exchange "
            + "         ON tradingItem.exchangeId = exchange.exchangeId "
            + "       JOIN [xf_target].[dbo].[ciqSymbol] symbol "
            + "         ON symbol.relatedCompanyId = company.companyId "
            + "       WHERE" // (:tickerSymbol IS NULL OR (tradingItem.tickerSymbol = :tickerSymbol AND exchange" +
            // ".exchangeSymbol = :exchangeSymbol)) and "
            + " (:symbol IS NULL OR (symbol.activeFlag = 1 AND :symbol = symbol.symbolValue AND ((:symbolType = " +
            "'CUSIP' AND symbol"
            + ".symbolTypeId = 15)" +
            " OR (:symbolType = 'ISIN' AND symbol.symbolTypeId = 14) OR (:symbolType = 'SEDOL' AND (symbol" +
            ".symbolTypeId = 22 OR symbol.symbolTypeId = 5701))))) "
            + "       and security.securityEndDate is null";


    NamedParameterJdbcTemplate jdbcTemplate;
    @Value("${sandp.company-search.wildcard-prefix}")
    private boolean wildcardCompanyPrefix = false;

    @Autowired
    public CompanyRepositoryImpl(DataSourceManager dataSourceManager) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSourceManager.dataSource("sandp"));
    }

    @SuppressWarnings({"serial", "unchecked"})
    @Override
    public Iterable<Company> findAll(String match, boolean publicCompany, boolean privateCompany,
                                     boolean publicPrivateCompany) {
        Map<String, ?> parameters = new HashMap<String, Object>() {
            {
                put("keyword", ((wildcardCompanyPrefix ? "%" : "") + match + '%'));
                put("publicCompany", publicCompany);
                put("privateCompany", privateCompany);
                put("publicPrivateCompany", publicPrivateCompany);
                put("exactValue", match);
            }
        };

        long startTime = System.nanoTime();

        SimpleJdbcCall query = new SimpleJdbcCall(jdbcTemplate.getJdbcTemplate()).withProcedureName(searchCompanyQuery)
                .withSchemaName("dbo")
                .declareParameters(new SqlParameter("keyword", Types.NVARCHAR),
                        new SqlParameter("publicCompany", Types.BIT), new SqlParameter("privateCompany", Types.BIT),
                        new SqlParameter("publicPrivateCompany", Types.BIT),
                        new SqlParameter("exactValue", Types.NVARCHAR))
                .returningResultSet("queryResults", QueryResult::parse);

        List<QueryResult> results = (List<QueryResult>) query.execute(parameters).get("queryResults");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Time taken to execute company query of:{} took:{}ms with {} items returned",
                    query.getCallString(), (System.nanoTime() - startTime) / 1000000,
                    results.size());
        }

        return createCompaniesRetainOrder(results);
    }

    private List<Company> createCompaniesRetainOrder(List<QueryResult> results) {
        Map<Long, Company> companies = createCompanies(results);

        return results.stream().map(x -> x.getCompanyId()).distinct().map(companyId -> companies.get(companyId))
                .collect(Collectors.toList());
    }

    private Map<Long, Company> createCompanies(List<QueryResult> results) {
        return results.stream()
                .collect(groupingBy(x -> x.getCompanyId(), collectingAndThen(toList(), this::createCompany)));
    }

    private Company createCompany(List<QueryResult> data) {
        QueryResult firstEntry = data.get(0);
        String companyShortName = data.stream().filter(x -> x.isSecurityPrimaryFlag() && x.isTradingItemPrimaryFlag())
                .map(x -> x.getTickerSymbol()).filter(y -> y != null).findFirst().orElse(firstEntry.getCompanyName());

        return new Company(firstEntry.getCompanyId(), firstEntry.getCompanyName(), companyShortName,
                firstEntry.getCompanyType(),
                new ArrayList<>(data.stream().filter(x -> x.getSecurityId() != null)
                        .collect(groupingBy(x -> x.getSecurityId(), collectingAndThen(toList(), this::createSecurity)))
                        .values()));
    }

    private Security createSecurity(List<QueryResult> data) {
        QueryResult firstEntry = data.get(0);
        return new Security(firstEntry.getSecurityId(), firstEntry.getSecurityName(),
                firstEntry.isSecurityPrimaryFlag(),
                new ArrayList<>(data.stream().filter(x -> x.getTradingItemId() != null).collect(
                        groupingBy(x -> x.getTradingItemId(), collectingAndThen(toList(), this::createTradingItem)))
                        .values()));
    }

    private TradingItem createTradingItem(List<QueryResult> data) {
        QueryResult firstEntry = data.get(0);
        return new TradingItem(firstEntry.getTradingItemId(), firstEntry.isTradingItemPrimaryFlag(),
                firstEntry.getTickerSymbol());
    }

    @Override
    public Optional<Company> findById(long id) {
        long start = 0;
        if (LOGGER.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }

        List<QueryResult> queryResults = jdbcTemplate.getJdbcTemplate().query(findCompanyByIdQuery, new Object[]{id},
                QueryResult::parse);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Time to read in Company with id:{}, took {}ms",
                    id, System.currentTimeMillis() - start);
        }

        return Optional.ofNullable(createCompanies(queryResults).get(id));
    }

    @Override
    public Iterable<Company> findAll(String tickerSymbol, String exchangeSymbol,
                                     String symbol, SymbolType symbolType) {
        List<QueryResult> queryResults = jdbcTemplate.query(findCompanyByTickerQuery,
                new HashMap<String, String>() {{
                    put("tickerSymbol", tickerSymbol);
                    put("exchangeSymbol", exchangeSymbol);
                    put("symbol", symbol);
                    put("symbolType", symbolType.toString());
                }},
                QueryResult::parse);

        return createCompanies(queryResults).values();
    }

    @Data
    static class QueryResult {
        long companyId;
        String companyName;
        String webPage;
        Long securityId;
        String securityName;
        boolean securityPrimaryFlag;
        String tickerSymbol;
        Long tradingItemId;
        long exchangeId;
        long currencyId;
        long tradingItemStatusId;
        boolean tradingItemPrimaryFlag;
        int companyType;

        public static QueryResult parse(ResultSet resultSet, int rowNum) throws SQLException {
            QueryResult result = new QueryResult();
            result.companyId = resultSet.getLong("companyId");
            result.companyName = resultSet.getString("companyName");
            result.webPage = resultSet.getString("webPage");
            result.securityId = resultSet.getObject("securityId") == null ? null : resultSet.getLong("securityId");
            result.securityName = resultSet.getString("securityName");
            result.securityPrimaryFlag = resultSet.getBoolean("securityPrimaryFlag");
            result.tickerSymbol = resultSet.getString("tickerSymbol");
            result.tradingItemId = resultSet.getObject("tradingItemId") == null ? null
                    : resultSet.getLong("tradingItemId");
            result.exchangeId = resultSet.getLong("exchangeId");
            result.currencyId = resultSet.getLong("currencyId");
            result.tradingItemStatusId = resultSet.getLong("tradingItemStatusId");
            result.tradingItemPrimaryFlag = resultSet.getBoolean("tradingItemPrimaryFlag");
            result.companyType = resultSet.getInt("companyType");
            return result;
        }
    }


}
