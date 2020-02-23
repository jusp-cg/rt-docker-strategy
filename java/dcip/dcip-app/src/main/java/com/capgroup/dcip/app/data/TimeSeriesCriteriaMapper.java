package com.capgroup.dcip.app.data;

import com.capgroup.dcip.app.common.EntityMapper;
import com.capgroup.dcip.app.common.ToEntity;
import com.capgroup.dcip.app.common.UtilityEntityMapper;
import com.capgroup.dcip.app.identity.UserModel;
import com.capgroup.dcip.app.identity.UserService;
import com.capgroup.dcip.app.reference.company.CompanyModel;
import com.capgroup.dcip.app.reference.company.CompanyService;
import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.domain.data.DateQueryFormat;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * Maps between a TimeSeriesQuery an a TimeSeriesQueryModel and a TimeSeriesUpdates and a
 * TimeSeriesAuditModel
 */
@Mapper(uses = EntityMapper.class)
public abstract class TimeSeriesCriteriaMapper {

    @Autowired
    private CompanyService companyService;
    private UserService userService;

    @Mappings({@Mapping(source = "seriesId", target = "series", qualifiedBy = {UtilityEntityMapper.class, ToEntity.class}),
    	@Mapping(source = "companyId", target = "company", qualifiedByName = "ToCompany"),
            @Mapping(source = "userId", target = "user", qualifiedByName = "ToUser")})
    public abstract TimeSeriesCriteria map(TimeSeriesCriteriaModel model);

    public abstract Iterable<TimeSeriesQuery> mapAll(Iterable<TimeSeriesQueryModel> model);

    @Mappings({@Mapping(source = "startDate", target = "dateRange.start", qualifiedByName = "ToStartDate"),
            @Mapping(source = "endDate", target = "dateRange.end", qualifiedByName = "ToEndDate")})
    @InheritConfiguration
    public abstract TimeSeriesQuery map(TimeSeriesQueryModel model);

    @Named("ToStartDate")
    protected LocalDateTime mapStartDate(String startDate) {
        return mapDate(startDate, LocalDateTimeRange.MIN_START);
    }

    protected LocalDateTime mapDate(String date, LocalDateTime defaultValue) {
        return date == null ? defaultValue : new DateQueryFormat().parse(date).toDateTime();
    }

    @Named("ToEndDate")
    protected LocalDateTime mapEndDate(String endDate) {
        return mapDate(endDate, LocalDateTimeRange.MAX_END);
    }

    @Named("ToCompany")
    protected CompanyModel mapCompany(long companyId) {
        return CompanyService.findByIdOrUnknown(companyService, companyId);
    }

    @Named("ToUser")
    protected UserModel mapUser(long userId) {
    	return UserService.findById(userService, userId);
    }
}
