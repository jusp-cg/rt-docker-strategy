package com.capgroup.dcip.app.data;

import com.capgroup.dcip.app.identity.IdentityMappingService;
import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.domain.data.AuditableTimeSeries;
import com.capgroup.dcip.domain.data.DataProperty;
import com.capgroup.dcip.domain.data.DataSourceConnection;
import com.capgroup.dcip.domain.data.TimeSeries;
import com.capgroup.dcip.sand.company.Company;
import com.capgroup.dcip.sand.company.SAndPCompanyGateway;
import com.capgroup.dcip.sand.company.TradingItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.inject.Named;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Named("TimeSeriesSAndPDataSourceConnection")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TimeSeriesSAndPDataSourceConnection implements TimeSeriesDataSourceConnection {

    private static final String FUNCTION_KEY = "__FUNCTION__";
    private static final String MNEUMONIC_KET = "__MNEUMONIC__";

    private static List<String> RESERVED_KEYWORDS = Arrays.asList(FUNCTION_KEY, MNEUMONIC_KET);
    private static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    @Value("${sAndPApi.url}")
    String url;

    @Autowired
    @Named("sAndPRestTemplate")
    private RestTemplate restTemplate;
    private DataSourceConnection dataSourceConnection;
    @Autowired
    private IdentityMappingService identityMappingService;
    @Autowired
    private SAndPCompanyGateway companyGateway;

    public TimeSeriesSAndPDataSourceConnection(DataSourceConnection dataSourceConnection) {
        this.dataSourceConnection = dataSourceConnection;
    }

    @Override
    public TimeSeriesDataSourceConnectionMatchResult matches(TimeSeriesCriteria query) {
        return new TimeSeriesDataSourceConnectionMatchResult(dataSourceConnection.isSeriesSupported(query.getSeries())
                ? new TimeSeriesDataSourceConnectionMatchResult.Feature[]{
                TimeSeriesDataSourceConnectionMatchResult.Feature.Series}
                : new TimeSeriesDataSourceConnectionMatchResult.Feature[0]);
    }

    @Override
    public Map<TimeSeriesQuery, CompletableFuture<TimeSeries>> query(Collection<TimeSeriesQuery> q, Executor executor) {
        List<TimeSeriesQuery> queries = q instanceof List ? (List<TimeSeriesQuery>) q : new ArrayList<>(q);

        CompletableFuture<SDKDataOutput> resultFuture = CompletableFuture.supplyAsync(() ->
        {
            SDKDataRequest request = new SDKDataRequest(queries.stream().map(this::map).collect(Collectors.toList()));

            return restTemplate.postForObject(url,
                    new HttpEntity<>(request),
                    SDKDataOutput.class);
        });

        return IntStream.range(0, q.size()).boxed()
                .collect(Collectors.toMap(index -> queries.get(index),
                        index -> resultFuture.thenApply(result ->
                                map(result.getResponse().get(index))
                        )));
    }

    protected TimeSeries map(Response response) {
        if (!StringUtils.isEmpty(response.getErrMsg()))
            throw new RuntimeException(response.getErrMsg());

        return TimeSeries.of(IntStream.range(0, response.getNumRows().intValue()).mapToObj(index ->
                new TimeSeries.Entry(LocalDateTime.parse(stripQuotes(response.getRows().get(index).getRow().get(1)),
                        DATE_FORMAT),
                        new BigDecimal(response.getRows().get(index).getRow().get(0)))
        ));
    }

    String stripQuotes(String str) {
        return str.replaceAll("'", "");
    }

    @Override
    public Map<TimeSeriesQuery, CompletableFuture<AuditableTimeSeries>> audits(Collection<TimeSeriesQuery> query,
                                                                               Executor executor) {
        return null;
    }

    InputRequest map(TimeSeriesQuery query) {
        /*
        InputRequest result = new InputRequest();
        result.setFunction("GDSP");
        result.setIdentifier("IBM:");
        result.setMnemonic("IQ_TOTAL_REV");
        result.setProperties(new HashMap<String, String>() {
            {
                put("PERIODTYPE", "IQ_FQ");
            }
        });

        return result;
        */
        return dataSourceConnection.getSeriesInDataSource(query.getSeries()).map(seriesMapping -> {
            InputRequest input = new InputRequest();

            List<DataProperty> properties = seriesMapping.resolveProperties().collect(Collectors.toList());

            // set the function
            input.setFunction(properties.stream().filter(x -> x.getKey().equalsIgnoreCase(FUNCTION_KEY)).findFirst()
                    .map(value -> value.getValue()).orElseThrow(() -> new IllegalArgumentException()));

            // set the mneumonic
            properties.stream().filter(x -> x.getKey().equalsIgnoreCase(MNEUMONIC_KET)).findFirst().ifPresent(value
                    -> input.setMnemonic(value.getValue()));

            // get the remainder of the properties
            Map<String, String> inputProperties =
                    properties.stream().filter(x -> !RESERVED_KEYWORDS.contains(x.getKey())).collect(Collectors.toMap
                            (DataProperty::getKey, DataProperty::getValue));
            if (inputProperties != null) {
                input.setProperties(inputProperties);
            }

            // get the S&P companyId
            String companyId = identityMappingService.externalIdentifier(query.getCompany().getId(), 1l, 14)
                    .orElseThrow(() -> new IllegalArgumentException());

            // get the company details
            Company sandpCompany =
                    companyGateway.findById(Long.parseLong(companyId)).orElseThrow(() -> new IllegalArgumentException
                            ("Unknown Company"));

            TradingItem primaryTradingItem =
                    sandpCompany.primarySecurity().map(x -> x.primaryTradingItem().orElseThrow(() -> new IllegalArgumentException("No primary TradingItem")))
                            .orElseThrow(() -> new IllegalArgumentException("No primary Security"));

            input.setIdentifier(primaryTradingItem.getSymbolTicker() + ":");

            // set the start date/end date
            if (query.getDateRange() != null) {
                if (input.properties == null) {
                    input.properties = new HashMap<>();
                }
                if (query.getDateRange().getStart() != null &&
                        !(query.getDateRange().getStart().equals(LocalDateTimeRange.MIN_START))) {
                    input.properties.put("startDate", DATE_FORMAT.format(query.getDateRange().getStart()));
                }
                if (query.getDateRange().getEnd() != null && !(query.getDateRange().getEnd().equals(LocalDateTimeRange.MAX_END))) {
                    input.properties.put("endDate", DATE_FORMAT.format(query.getDateRange().getEnd()));
                }
            }

            return input;
        }).orElseThrow(() -> new IllegalArgumentException());
    }

    @Data
    @AllArgsConstructor
    static class SDKDataRequest {
        List<InputRequest> inputRequests = new ArrayList<>();
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
    static class Row {
        List<String> row;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
    static class SDKDataOutput {
        @JsonProperty("GDSSDKResponse")
        private List<Response> response;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
    static class Response {
        private List<String> headers;
        private List<Row> rows;
        private Long numCols;
        private String seniority;
        private String mnemonic;
        private String function;
        private String errMsg;
        private Map<String, String> properties;
        private LocalDate startDate;
        private Long numRows;
        private String cacheExpiryTime;
        private String snapType;
        private String frequency;
        private String identifier;
        private String limit;
    }

    @Data
    static class InputRequest {
        private String function;
        private String identifier;
        private String mnemonic;
        private Map<String, String> properties;
    }
}
