package com.capgroup.dcip.sand.company;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.capgroup.dcip.util.data.DataSourceManager;

import lombok.Data;

/**
 * Implementation for accessing Company from the SAndP database
 */
@Component
public class CompanySummaryRepositoryImpl implements CompanySummaryRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompanySummaryRepositoryImpl.class);

	private static String getCompanySummaryHeadersQuery = "dcipCompanySummaryHeaders";
	private static String getCompanySummaryReportQuery = "dcipCompanySummaryReport";


	//Company Summary -- contains two parts
	//  Part 1 - Company header
	//  Part 2 - Company financial report
	
	@Data
	static class QueryHeaderResult {
		//Company Report Header Variables
		 long companyId;
		 String companyName;
		 String countryLoc;
		 String tradingCurrency;
		 String companyTicker;
		 String marketCap;
		 String exchangeRate;
		 String exchange;
		 String percentFloat;
		 String latestPrice;
		 String dividendPerShare;
		 String reportingCurrency;
		 String reportingExchangeRate;
		 String dailyVolume;
		 String companyDescription;
		 String fiscalYearEnd;
			
		public static QueryHeaderResult parse(ResultSet resultSet, int rowNum) throws SQLException {
			QueryHeaderResult result = new QueryHeaderResult();
			result.companyId = resultSet.getLong("companyId");
			result.companyName = resultSet.getString("companyName");
			result.countryLoc = resultSet.getString("countryLoc");
			result.tradingCurrency = resultSet.getString("tradingCurrency");
			result.companyTicker = resultSet.getString("companyTicker");
			result.marketCap = resultSet.getString("marketCap");
			result.exchangeRate = resultSet.getString("exchangeRate");
			result.exchange = resultSet.getString("exchange");
			result.percentFloat = resultSet.getString("percentFloat");
			result.latestPrice = resultSet.getString("latestPrice");
			result.dividendPerShare = resultSet.getString("dividendPerShare");
			result.reportingCurrency = resultSet.getString("reportingCurrency");
			result.reportingExchangeRate = resultSet.getString("reportingExchangeRate");
			result.dailyVolume = resultSet.getString("dailyVolume");
			result.companyDescription = resultSet.getString("companyDescription");
			//remove the newline and return characters
			result.companyDescription = result.companyDescription.replace("\n", " ").replace("\r", " ");
			result.fiscalYearEnd = resultSet.getString("fiscalYearEnd");
			
			return result;
		}
	}
	
	@Data
	static class QueryReportResult {
		//Company Report Header Variables
		 List<String> reportItem;	 
		 Map<String, List<String>> historicData;
		 
		public static QueryReportResult parse(ResultSet resultSet, int rowNum) throws SQLException {
			QueryReportResult result = new QueryReportResult();
			
			 List<String> cols = new ArrayList<String>();
			 List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			 
			 // The report variables can be changed within the SQL as more requirements come in
			 // We parse the ResultSet dynamically

			 // get column names
			 ResultSetMetaData meta = resultSet.getMetaData();		 
				 for (int i = 1; i <= meta.getColumnCount(); i++) {
				 		String col = meta.getColumnName(i);
				 		cols.add(col);
				 	}
			 
			// get column values
			 	do{		        	
		        	Map<String, String> map = new HashMap<String, String>();
		            for (int j = 1; j <= meta.getColumnCount(); j++) {
		                String key = meta.getColumnName(j);
		                String value = resultSet.getString(key);
		                map.put(key, value);
		            }
		            list.add(map);
		        } while (resultSet.next());
			 	
			 
			 	
			 // Transforming the ResultSet in Java is quicker than in the front end
			 //	 And we will eventually be adding a 3rd result set of "financial estimates"
			 //  *** GVDN tech debt #2 - this can be done above if we set TYPE_SCROLL_SENSITIVE on the ResultSet
			 	
			 	Map<String, List<String>> table = new HashMap<String, List<String>>();			 	
			 	
			 	for (Map.Entry<String, String> pair : list.get(0).entrySet()) {
			 		String newKey = pair.getKey();
			 		List<String> vals = new ArrayList<String>();
			 		for(int i = 0; i < list.size(); i++) {
			 			String newVal = list.get(i).get(newKey);
			 			vals.add(newVal);
			 		}
			 		table.put(newKey, vals);
			 	}
		      result.reportItem = cols;
		      result.historicData = table;
		
			return result;
		}
	}

	
	
	
	
	
	JdbcTemplate jdbcTemplate;

	@Autowired
	public CompanySummaryRepositoryImpl(DataSourceManager dataSourceManager) {
		jdbcTemplate = new JdbcTemplate(dataSourceManager.dataSource("sandp"));
	}

	
	@SuppressWarnings({ "serial", "unchecked" })
	
	
	private CompanySummary createCompanySummary(List<QueryHeaderResult> head, List<QueryReportResult> report) {
		QueryHeaderResult firstHeaderEntry = head.get(0);
		QueryReportResult firstReportEntry = report.get(0);
		
		CompanySummary compSummary = new CompanySummary();
			compSummary.setCompanyId(firstHeaderEntry.companyId);
			compSummary.setCompanyName(firstHeaderEntry.companyName);
			compSummary.setCountryLoc(firstHeaderEntry.countryLoc);
			compSummary.setTradingCurrency(firstHeaderEntry.tradingCurrency);
			compSummary.setCompanyTicker(firstHeaderEntry.companyTicker);
			compSummary.setMarketCap(firstHeaderEntry.marketCap);
			compSummary.setExchangeRate(firstHeaderEntry.exchangeRate);
			compSummary.setExchange(firstHeaderEntry.exchange);
			compSummary.setPercentFloat(firstHeaderEntry.percentFloat);
			compSummary.setLatestPrice(firstHeaderEntry.latestPrice);
			compSummary.setDividendPerShare(firstHeaderEntry.dividendPerShare);
			compSummary.setReportingCurrency(firstHeaderEntry.reportingCurrency);
			compSummary.setReportingExchangeRate(firstHeaderEntry.reportingExchangeRate);
			compSummary.setDailyVolume(firstHeaderEntry.dailyVolume);
			compSummary.setCompanyDescription(firstHeaderEntry.companyDescription);
			compSummary.setFiscalYearEnd(firstHeaderEntry.fiscalYearEnd);
			
			compSummary.setReportItem(firstReportEntry.reportItem);
			compSummary.setHistoricData(firstReportEntry.historicData);
				
		 return compSummary;
	}
	
	@Override
	public Optional<CompanySummary> findSummaryById(long id) {
		long start = 0;
		if (LOGGER.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		
		// Query parameters (Company Id from query)
		Map<String, ?> parameters = new HashMap<String, Object>() {
			{
				put("companyId", id);
			}
		};
		
		SimpleJdbcCall headerQuery = new SimpleJdbcCall(jdbcTemplate).withProcedureName(getCompanySummaryHeadersQuery)
				.withSchemaName("dbo")
				.declareParameters(new SqlParameter("companyId", Types.INTEGER))
				.returningResultSet("headerResults", QueryHeaderResult::parse);

		List<QueryHeaderResult> queryHeaderResults = (List<QueryHeaderResult>) headerQuery.execute(parameters).get("headerResults");
		
		
		SimpleJdbcCall reportQuery = new SimpleJdbcCall(jdbcTemplate).withProcedureName(getCompanySummaryReportQuery)
				.withSchemaName("dbo")
				.declareParameters(new SqlParameter("companyId", Types.INTEGER))
				.returningResultSet("reportResults", QueryReportResult::parse);

		List<QueryReportResult> queryReportResults = (List<QueryReportResult>) reportQuery.execute(parameters).get("reportResults");
		
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Time to read in Company with id:{}, took {}ms",
					id, System.currentTimeMillis() - start);
		}
		
		return Optional.ofNullable(createCompanySummary(queryHeaderResults, queryReportResults));
			

	}


}
	