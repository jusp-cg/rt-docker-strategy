package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.data.QUserTimeSeries;
import com.capgroup.dcip.domain.data.UserTimeSeries;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTimeSeriesRepository extends TemporalEntityRepository<UserTimeSeries>,
        QuerydslPredicateExecutor<UserTimeSeries> {


    /**
     * Builder class for creating DSL expressions
     */
    class ExpressionBuilder extends TemporalEntityRepository.ExpressionBuilder {

        QUserTimeSeries userTimeSeries;

        public ExpressionBuilder() {
            this(QUserTimeSeries.userTimeSeries);
        }

        public ExpressionBuilder(QUserTimeSeries timeSeries) {
            super(timeSeries._super);
            this.userTimeSeries = timeSeries;
        }

        public BooleanExpression WithCompany(long companyId) {
            return userTimeSeries.companyId.eq(companyId);
        }

        public BooleanExpression WithSeries(long seriesId) {
            return userTimeSeries.series.id.eq(seriesId);
        }

        public BooleanExpression withUser(long userId) {
            return userTimeSeries.user.id.eq(userId);
        }
    }

}
