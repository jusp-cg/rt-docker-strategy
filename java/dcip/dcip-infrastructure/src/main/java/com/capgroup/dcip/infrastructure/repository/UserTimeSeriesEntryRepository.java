package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.data.QUserTimeSeriesEntry;
import com.capgroup.dcip.domain.data.UserTimeSeries;
import com.capgroup.dcip.domain.data.UserTimeSeriesEntry;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserTimeSeriesEntryRepository extends TemporalEntityRepository<UserTimeSeriesEntry>,
        QuerydslPredicateExecutor<UserTimeSeriesEntry> {

    List<UserTimeSeriesEntry> findAllByUserTimeSeriesAndTimestampGreaterThanEqualAndTimestampLessThanEqual(
            UserTimeSeries timeSeries, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Builder class for creating DSL expressions
     */
    class ExpressionBuilder extends TemporalEntityRepository.ExpressionBuilder {

        QUserTimeSeriesEntry userTimeSeriesEntry;

        public ExpressionBuilder() {
            this(QUserTimeSeriesEntry.userTimeSeriesEntry);
        }

        public ExpressionBuilder(QUserTimeSeriesEntry timeSeriesEntry) {
            super(timeSeriesEntry._super);
            this.userTimeSeriesEntry = timeSeriesEntry;
        }

        public BooleanExpression hasUserTimeSeries(long userTimeSeriesId) {
            return userTimeSeriesEntry.userTimeSeries.id.eq(userTimeSeriesId);
        }

        public BooleanExpression hasTimeStamps(List<LocalDateTime> timestamps) {
            return userTimeSeriesEntry.timestamp.in(timestamps);
        }
    }
}
