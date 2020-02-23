package com.capgroup.dcip.app.data;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.app.reference.company.CompanyModel;
import com.capgroup.dcip.app.reference.company.CompanyService;
import com.capgroup.dcip.domain.data.*;
import com.capgroup.dcip.infrastructure.repository.SeriesRepository;
import com.capgroup.dcip.infrastructure.repository.UserRepository;
import com.capgroup.dcip.infrastructure.repository.UserTimeSeriesEntryRepository;
import com.capgroup.dcip.infrastructure.repository.UserTimeSeriesRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Executes a time series queryAll and creates the results
 */
@Service
@Slf4j
public class TimeSeriesServiceImpl implements TimeSeriesService {
    private TimeSeriesMapper timeSeriesMapper;
    private TimeSeriesFunctionFactory timeSeriesFunctionFactory;
    private TimeSeriesCriteriaMapper timeSeriesCriteriaMapper;
    private TimeSeriesDataSourceConnectionResolver dataSourceConnectionResolver;
    private UserTimeSeriesRepository userTimeSeriesRepository;
    private UserRepository userRepository;
    private SeriesRepository seriesRepository;
    private UserTimeSeriesEntryRepository userTimeSeriesEntryRepository;
    private RequestContextService requestContextService;
    private CompanyService companyService;
    // using an executor service incase we want to limit the number of queries running in parallel
    private ExecutorService executorService = ForkJoinPool.commonPool();

    @Autowired
    public TimeSeriesServiceImpl(TimeSeriesDataSourceConnectionResolver dataSourceConnectionResolver,
                                 TimeSeriesFunctionFactory factory, TimeSeriesMapper timeSeriesMapper,
                                 TimeSeriesCriteriaMapper timeSeriesCriteriaMapper,
                                 UserTimeSeriesRepository userTimeSeriesRepository,
                                 UserRepository userRepository,
                                 SeriesRepository seriesRepository,
                                 UserTimeSeriesEntryRepository userTimeSeriesEntryRepository,
                                 RequestContextService requestContextService,
                                 CompanyService companyService) {
        this.timeSeriesFunctionFactory = factory;
        this.dataSourceConnectionResolver = dataSourceConnectionResolver;
        this.timeSeriesMapper = timeSeriesMapper;
        this.timeSeriesCriteriaMapper = timeSeriesCriteriaMapper;
        this.userTimeSeriesRepository = userTimeSeriesRepository;
        this.userRepository = userRepository;
        this.seriesRepository = seriesRepository;
        this.userTimeSeriesEntryRepository = userTimeSeriesEntryRepository;
        this.requestContextService = requestContextService;
        this.companyService = companyService;
    }

    @SuppressWarnings("serial")
    TimeSeries postRead(TimeSeriesQuery query, TimeSeries timeSeries) {
        if (query.getStartOffset() == null) {
            return timeSeries;
        } else {
            TimeSeriesFunction function = timeSeriesFunctionFactory.create(new HashMap<String, Object>() {
                {
                    put("dayCount", (Object) query.getStartOffset());
                    put("startDate", query.getDateRange().getStart());
                }
            });
            return timeSeries.apply(function);
        }
    }

    @Override
    @Transactional
    public TimeSeriesModel<TimeSeries.Entry> delete(TimeSeriesQueryModel queryModel) {
        UserTimeSeriesRepository.ExpressionBuilder builder = new UserTimeSeriesRepository.ExpressionBuilder();
        return timeSeriesMapper.map(userTimeSeriesRepository.findOne(builder.WithCompany(queryModel.getCompanyId()).and(builder.WithSeries(queryModel.getSeriesId())).and(builder.withUser(queryModel.getUserId())))
                .map(timeSeries ->
                {
                    DateQueryFormat format = new DateQueryFormat();
                    List<UserTimeSeriesEntry> entries =
                            userTimeSeriesEntryRepository.findAllByUserTimeSeriesAndTimestampGreaterThanEqualAndTimestampLessThanEqual(timeSeries,
                                    format.parse(queryModel.getStartDate()).toDateTime(),
                                    format.parse(queryModel.getEndDate()).toDateTime());
                    TimeSeries result = TimeSeries.of(entries.stream().map(x -> x.toTimeSeriesEntry()));
                    userTimeSeriesEntryRepository.deleteAll(entries);
                    return result;
                }).orElse(TimeSeries.empty()));
    }

    Map<TimeSeriesQuery, CompletableFuture<TimeSeries>> executeQuery(
            TimeSeriesDataSourceConnection connection,
            Collection<TimeSeriesQuery> queries) {
        Map<TimeSeriesQuery, CompletableFuture<TimeSeries>> result = connection.query(queries, executorService);
        return result.entrySet().stream().collect(
                Collectors.toMap(
                        y -> y.getKey(),
                        y -> y.getValue().thenApply(t -> postRead(y.getKey(), t))));
    }

    @Override
    @Transactional
    public void deleteAll(List<TimeSeriesQueryModel> deletes) {
        // deleteAll the time series for each of the delete models
        deletes.forEach(delete ->{
            delete(delete);
        });
    }

    @Override
    public Iterable<TimeSeriesQueryResultModel<TimeSeries.Entry>> queryAll(Collection<TimeSeriesQueryModel> modelQueries) {
        // findAll the time series for each of the queries
        return timeSeries(modelQueries, this::executeQuery,
                timeSeries ->
                        timeSeriesMapper.map(timeSeries)
        );
    }

    @Override
    public TimeSeriesModel<TimeSeries.Entry> query(TimeSeriesQueryModel query) {
        return oneEntry(queryAll(Collections.singleton(query)).iterator());
    }

    protected <T extends TimeSeries.Entry> TimeSeriesModel<T> oneEntry(Iterator<TimeSeriesQueryResultModel<T>> items){
        if (items.hasNext()) {
            TimeSeriesQueryResultModel<T> resultModel = items.next();
            if (resultModel.getErrorMessage() == null){
                return resultModel.getTimeSeries();
            }
            throw new RuntimeException(resultModel.getErrorMessage());
        }

        throw new RuntimeException("No result for query");
    }

    @Override
    public TimeSeriesModel<AuditableTimeSeries.AuditableEntry> audit(TimeSeriesQueryModel query) {
        return oneEntry(auditAll(Collections.singleton(query)).iterator());
    }

    /**
     * Template algorithm for executing a timeseries queryAll/audit
     */
    <T extends TimeSeries,
            E extends TimeSeries.Entry> Iterable<TimeSeriesQueryResultModel<E>>
    timeSeries(Collection<TimeSeriesQueryModel> queryModels,
               BiFunction<TimeSeriesDataSourceConnection,
                       Collection<TimeSeriesQuery>,
                       Map<TimeSeriesQuery, CompletableFuture<T>>> executor,
               Function<T,
                       TimeSeriesModel<E>> mapper) {

        Map<TimeSeriesQuery, TimeSeriesQueryModel> queries = queryModels.stream().collect(Collectors
                .toMap(timeSeriesCriteriaMapper::map, Function.identity()));

        // group the queries by connection
        Map<TimeSeriesDataSourceConnection, List<TimeSeriesQuery>> queriesGroupedByDataSourceConnection =
                groupByConnection(queries.keySet());

        // execute all the queries
        Stream<Map<TimeSeriesQuery, CompletableFuture<T>>> futures =
                queriesGroupedByDataSourceConnection.entrySet().stream().map(entry ->
                        executor.apply(entry.getKey(), entry.getValue()));

        // handle the errors and get the result
        List<TimeSeriesQueryResultModel<E>> results =
                futures.flatMap(map -> map.entrySet().stream()).map(value -> value.getValue().handle((timeSeries,
                                                                                                      exc) -> {
                    if (exc != null) {
                        log.error("Unexpected exception", exc);
                        return new TimeSeriesQueryResultModel<E>(queries.get(value.getKey()), exc.getMessage());
                    } else {
                        return new TimeSeriesQueryResultModel<E>(queries.get(value.getKey()), mapper.apply(timeSeries));
                    }
                }).join()).collect(Collectors.toList());

        return results;



/*
        // group the queries into buckets of series/listing
        Map<TimeSeriesCriteria, List<TimeSeriesQuery>> groupByKey = queries.stream()
                .collect(groupingBy(x -> new TimeSeriesCriteria(x.getSeries(), x.getCompany(), x.getUser())));

        // for each series/listing merge the dates
        Map<TimeSeriesCriteria, Stream<LocalDateTimeRange>> result =
                groupByKey.entrySet().stream().collect(Collectors.toMap(x -> x.getKey(),
                        s -> LocalDateTimeRange.merge(s.getValue().stream().map(y -> y.getDateRange()))));

        // for each date range in each bucket retrieve the data. This can be executed in
        // parallel
        Map<TimeSeriesQuery, TimeSeries> timeSeries = result.entrySet().parallelStream().flatMap(x -> {
            // key and date range
            List<TimeSeriesQuery> queriesForKey = groupByKey.get(x.getKey());

            // calculate the start offset - this is the max offset for each of the queries
            // that have the same key and the start date intersects with the merged queryAll
            // Not a very good solution but will work for 90% of the input
            return x.getValue().map(value -> {
                return new TimeSeriesQuery(value, x.getKey().getSeries(), x.getKey().getCompany(), x.getKey().getUser(),
                        queriesForKey.stream().filter(y -> y.getDateRange().intersects(value))
                                .filter(y -> y.getStartOffset() != null).map(y -> y.getStartOffset())
                                .max(Comparator.comparing(Function.identity())).orElse(null));
            });
        }).collect(Collectors.toMap(Function.identity(), queryAll -> {
            // findAll the appropriate connection to process the queryAll
            TimeSeriesDataSourceConnection connection = dataSourceConnectionResolver.resolve(queryAll)
                    .orElseThrow(() -> new RuntimeException("Unresolved TimeSeriesDataSourceConnection"));

            // execute the queryAll
            return connection.queryAll(queryAll);
        }));

        // mapEntity back the input parameters to the appropriate TimeSeries (or part of
        // TimeSeries)
        return queries.stream()
                .collect(Collectors.toMap(Function.identity(),

                        query -> timeSeries.entrySet().stream()
                                .filter(x -> query.getDateRange().intersects(x.getKey().getDateRange())
                                        && x.getKey().getCompany().equals(query.getCompany())
                                        && x.getKey().getSeries().equals(query.getSeries())
                                        && Objects.equals(x.getKey().getUser(), query.getUser())) // User can be null with no problem
                                .map(x -> x.getValue()).findFirst().orElse(TimeSeries.empty())));
                              */
    }


    Map<TimeSeriesDataSourceConnection, List<TimeSeriesQuery>> groupByConnection(Collection<TimeSeriesQuery> queries) {
        // group the input by the DataSourceConnection
        return queries.stream().collect(Collectors.groupingBy(query -> resolveConnection(query)));
    }

    TimeSeriesDataSourceConnection resolveConnection(TimeSeriesQuery query) {
        // find the appropriate connection for the queryAll
        return dataSourceConnectionResolver.resolve(query)
                .orElseThrow(() -> new RuntimeException("Unresolved TimeSeriesDataSourceConnection"));
    }

    Map<TimeSeriesQuery, CompletableFuture<AuditableTimeSeries>> executeAudit(
            TimeSeriesDataSourceConnection connection,
            Collection<TimeSeriesQuery> queries) {
        return connection.audits(queries, executorService);
    }


    @Override
    public Iterable<TimeSeriesQueryResultModel<AuditableTimeSeries.AuditableEntry>> auditAll(Collection<TimeSeriesQueryModel> updates) {
        return timeSeries(updates, this::executeAudit, timeSeries -> timeSeriesMapper.map(timeSeries));
    }

    @Override
    @Transactional
    public void update(List<TimeSeriesUpdateModel> updates) {

        updates.forEach(update -> {

            resolveCompanyId(update.getTimeSeries());

            // find or create the UserTimeSeries
            UserTimeSeries userTimeSeries =
                    findUserTimeSeries(update.getTimeSeries()).orElseGet(() -> createUserTimeSeries(update.getTimeSeries()));

            // find existing entries
            List<LocalDateTime> updateTimestamps =
                    update.getData().getValues().stream().map(x -> getUtcTime(x.getTimestamp())).collect(Collectors.toList());
            UserTimeSeriesEntryRepository.ExpressionBuilder builder =
                    new UserTimeSeriesEntryRepository.ExpressionBuilder();
            BooleanExpression expression =
                    builder.hasTimeStamps(updateTimestamps).and(builder.hasUserTimeSeries(userTimeSeries.getId()));
            Iterable<UserTimeSeriesEntry> existingEntries = userTimeSeriesEntryRepository.findAll(expression);

            // update the existing entries
            StreamSupport.stream(existingEntries.spliterator(), false).forEach(existingEntry -> {
                update.getData().getValues().stream().filter(x -> getUtcTime(x.getTimestamp()).equals(existingEntry.getTimestamp())).findAny().ifPresent(x -> existingEntry.setValue(x.getValue()));
            });

            // create new entries
            update.getData().getValues().stream().filter(newItem ->
                    !StreamSupport.stream(existingEntries.spliterator(), false).anyMatch(x -> x.getTimestamp().equals(getUtcTime(newItem.getTimestamp()))))
                    .map(newItem ->
                            userTimeSeriesEntryRepository.save(new UserTimeSeriesEntry(getUtcTime(newItem.getTimestamp()),
                                    newItem.getValue(), userTimeSeries))
                    ).collect(Collectors.toList());
        });
    }

    void resolveCompanyId(TimeSeriesCriteriaModel timeSeriesCriteriaModel){
        // set the company id
        if (timeSeriesCriteriaModel.getCompanyId() == null) {
            CompanyModel all = StreamSupport.stream(companyService.findAll(null, null, timeSeriesCriteriaModel.getSymbol(),
                    timeSeriesCriteriaModel.getSymbolType()).spliterator(), false).findFirst().orElseThrow(() -> new ResourceNotFoundException("Company",
                    timeSeriesCriteriaModel.getSymbol()));
            timeSeriesCriteriaModel.setCompanyId(all.getId());
        }
    }

    Optional<UserTimeSeries> findUserTimeSeries(TimeSeriesCriteriaModel timeSeries) {
        // find/create the UserTimeSeries
        UserTimeSeriesRepository.ExpressionBuilder builder = new UserTimeSeriesRepository.ExpressionBuilder();
        BooleanExpression expression =
                builder.WithCompany(timeSeries.getCompanyId()).and(builder.WithSeries(timeSeries.getSeriesId())).and(builder.withUser(getUserId(timeSeries)));

        return userTimeSeriesRepository.findOne(expression);
    }

    long getUserId(TimeSeriesCriteriaModel model) {
        return model.getUserId() == null
                ? requestContextService.currentProfile().getUser().getId() : model.getUserId();
    }

    UserTimeSeries createUserTimeSeries(TimeSeriesCriteriaModel model) {
        UserTimeSeries userTimeSeries = new UserTimeSeries(userRepository.findByIdUnchecked(getUserId(model)),
                model.getCompanyId(), seriesRepository.findById(model.getSeriesId())
                .orElseThrow(() -> new ResourceNotFoundException("Series", String.valueOf(model.getSeriesId()))));
        return userTimeSeriesRepository.save(userTimeSeries);
    }

    LocalDateTime getUtcTime(LocalDateTime timestamp){
        return LocalDateTime.of(timestamp.toLocalDate(), LocalTime.MIDNIGHT);
    }
}
