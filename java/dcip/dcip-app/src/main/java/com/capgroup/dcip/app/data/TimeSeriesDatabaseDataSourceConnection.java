package com.capgroup.dcip.app.data;

import com.capgroup.dcip.app.identity.IdentityMappingService;
import com.capgroup.dcip.app.reference.company.CompanyModel;
import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.domain.data.*;
import com.capgroup.dcip.domain.identity.User;
import com.capgroup.dcip.util.SQLUtil;
import com.capgroup.dcip.util.data.DataSourceManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.capgroup.dcip.util.Lazy.lazy;

/**
 * Provides the functionality to query against a database to retrieve a time
 * series
 */
@Slf4j
@Service
@Named("TimeSeriesDatabaseDataSourceConnection")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TimeSeriesDatabaseDataSourceConnection implements TimeSeriesDataSourceConnection {

    @Component
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    static class Executor {

        private DataSourceConnection dataSourceConnection;
        private JdbcTemplate jdbcTemplate;
        private IdentityMappingService identityMappingService;
        private DataSourceManager dataSourceManager;

        @Autowired
        public Executor(IdentityMappingService identityMappingService, DataSourceManager dataSourceManager) {
            this.identityMappingService = identityMappingService;
            this.dataSourceManager = dataSourceManager;
        }

        public void init(DataSourceConnection dataSourceConnection) {
            this.dataSourceConnection = dataSourceConnection;
            jdbcTemplate = new JdbcTemplate(dataSourceManager.dataSource(dataSourceConnection.getConnectionName()));
        }

        // store the expression so it doesn't need to be recalculated
        private Supplier<Expression> queryExpression = lazy(() -> {
            ExpressionParser parser = new SpelExpressionParser();
            return parser.parseExpression(dataSourceConnection.getQueryString(),
                    new TemplateParserContext());
        });

        private Supplier<Expression> updatesExpression = lazy(() -> {
            ExpressionParser parser = new SpelExpressionParser();
            return parser.parseExpression(dataSourceConnection.getUpdatesString(),
                    new TemplateParserContext());
        });


        /**
         * Highest level of concurrency
         */
        @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
        public TimeSeries query(TimeSeriesQuery query) {
            String strQuery = createTimeSeriesQuery(query);

            return TimeSeries.of(executeQuery(strQuery, TimeSeriesDatabaseDataSourceConnection::createTimeSeries));
        }

        private String createTimeSeriesUpdatesQuery(TimeSeriesQuery query) {

            @AllArgsConstructor
            @Data
            @Value
            class SQLDateRange {
                String startDate;
                String endDate;
            }

            @AllArgsConstructor
            @Data
            @Value
            class TimeSeriesParserContext {
                Series series;
                CompanyModel company;
                User user;
                Map<String, String> properties;
                SQLDateRange dateRange;

                @SuppressWarnings("unused")
                public String identifier(long internalId, long entityType) {
                    return identityMappingService
                            .externalIdentifier(internalId, dataSourceConnection.getDataSource(), entityType).get();
                }
            }

            SeriesMapping seriesMapping = dataSourceConnection.getSeriesInDataSource(query.getSeries()).get();

            // create the context
            TimeSeriesParserContext cntxt = new TimeSeriesParserContext(query.getSeries(), query.getCompany(),
                    query.getUser(),
                    DataProperty.toMap(seriesMapping.resolveProperties()),
                    new SQLDateRange(
                            SQLUtil.toSQLDateTimeFormat(query.getDateRange().getStart()),
                            SQLUtil.toSQLDateTimeFormat(query.getDateRange().getEnd())));

            // parse the query
            String result = updatesExpression.get().getValue(new StandardEvaluationContext(cntxt), String.class);

            return result;

        }

        private String createTimeSeriesQuery(TimeSeriesQuery query) {

            long startTime = 0;
            if (log.isDebugEnabled()) {
                startTime = System.currentTimeMillis();
            }

            @AllArgsConstructor
            @Data
            @Value
            class SQLDateRange {
                String startDate;
                String endDate;
                Integer startOffset;
            }

            @AllArgsConstructor
            @Data
            @Value
            class TimeSeriesParserContext {
                SQLDateRange dateRange;
                Series series;
                CompanyModel company;
                User user;
                Map<String, String> properties;

                @SuppressWarnings("unused")
                public String identifier(long internalId, long entityType) {
                    return identityMappingService
                            .externalIdentifier(internalId, dataSourceConnection.getDataSource(), entityType).get();
                }
            }

            // convert the date range to string
            LocalDateTimeRange boundedDateRange = query.getDateRange();
            SQLDateRange dateRange = new SQLDateRange(SQLUtil.toSQLDateFormat(boundedDateRange.getStart()),
                    SQLUtil.toSQLDateFormat(boundedDateRange.getEnd()), query.getStartOffset());

            SeriesMapping seriesMapping = dataSourceConnection.getSeriesInDataSource(query.getSeries()).get();

            // create the context
            TimeSeriesParserContext cntxt = new TimeSeriesParserContext(dateRange, query.getSeries(), query.getCompany(),
                    query.getUser(),
                    DataProperty.toMap(seriesMapping.resolveProperties()));

            // parse the query
            String result = queryExpression.get().getValue(new StandardEvaluationContext(cntxt), String.class);

            if (log.isDebugEnabled()) {
                log.debug("Time to generate timeseries query:{} took {}ms", result,
                        System.currentTimeMillis() - startTime);
            }
            return result;
        }

        private <T extends TimeSeries.Entry> Collection<T> executeQuery(String strQuery,
                                                                        RowMapper<T> rowMapper) {
            long startTime = 0;
            if (log.isDebugEnabled()) {
                startTime = System.currentTimeMillis();
            }

            if (log.isDebugEnabled()) {
                log.debug("Executing query:" + strQuery);
            }

            Collection<T> timestamps = jdbcTemplate.query(strQuery,
                    rowMapper);

            if (log.isDebugEnabled()) {
                log.debug("Time to execute timeseries query:{}ms", System.currentTimeMillis() - startTime);
            }

            return timestamps;
        }

        @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
        public AuditableTimeSeries audits(TimeSeriesQuery query) {
            String updateQuery = createTimeSeriesUpdatesQuery(query);

            Collection<AuditableTimeSeries.AuditableEntry> data = executeQuery(updateQuery,
                    TimeSeriesDatabaseDataSourceConnection::createAuditableTimeSeries);

            // if there are multiple items with the same timestamp - choose the one with the latest modified
            Stream<AuditableTimeSeries.AuditableEntry> distinctData =
                    data.stream().collect(Collectors.groupingBy(AuditableTimeSeries.AuditableEntry::getTimestamp)).entrySet().stream().map(entry ->
                            {
                                if (entry.getValue().size() == 1)
                                    return entry.getValue().get(0);
                                return entry.getValue().stream().sorted(Comparator.comparing(AuditableTimeSeries.AuditableEntry::getModifiedTimestamp).reversed())
                                        .findFirst().get();
                            }
                    );

            return AuditableTimeSeries.of(distinctData);
        }
    }

    DataSourceConnection dataSourceConnection;
    @Autowired
    Executor queryExecutor;

    public TimeSeriesDatabaseDataSourceConnection(DataSourceConnection dataSourceConnection) {
        this.dataSourceConnection = dataSourceConnection;
    }

    @PostConstruct
    public void init() {
        queryExecutor.init(dataSourceConnection);
    }

    private static TimeSeries.Entry createTimeSeries(ResultSet resultSet, int row) throws SQLException {
        return new TimeSeries.Entry(resultSet.getTimestamp(1).toLocalDateTime(), resultSet.getBigDecimal(2));
    }

    private static AuditableTimeSeries.AuditableEntry createAuditableTimeSeries(ResultSet resultSet, int row) throws SQLException {
        return new AuditableTimeSeries.AuditableEntry(resultSet.getTimestamp(1).toLocalDateTime(),
                resultSet.getBigDecimal(2), resultSet.getTimestamp(3).toLocalDateTime());
    }

    @Override
    public TimeSeriesDataSourceConnectionMatchResult matches(TimeSeriesCriteria query) {
        return new TimeSeriesDataSourceConnectionMatchResult(dataSourceConnection.isSeriesSupported(query.getSeries())
                ? new TimeSeriesDataSourceConnectionMatchResult.Feature[]{
                TimeSeriesDataSourceConnectionMatchResult.Feature.Series}
                : new TimeSeriesDataSourceConnectionMatchResult.Feature[0]);
    }

    @Override
    public Map<TimeSeriesQuery, CompletableFuture<TimeSeries>> query(Collection<TimeSeriesQuery> queries,
                                                                     java.util.concurrent.Executor executor) {
        return queries.stream().collect(Collectors.toMap(Function.identity(),
                q -> CompletableFuture.supplyAsync(() ->
                                queryExecutor.query(q)
                        , executor)));
    }

    @Override
    public Map<TimeSeriesQuery, CompletableFuture<AuditableTimeSeries>> audits(Collection<TimeSeriesQuery> queries, java.util.concurrent.Executor executor) {
        return queries.stream().collect(Collectors.toMap(Function.identity(),
                q -> CompletableFuture.supplyAsync(() ->
                                queryExecutor.audits(q)
                        , executor)));
    }
}
