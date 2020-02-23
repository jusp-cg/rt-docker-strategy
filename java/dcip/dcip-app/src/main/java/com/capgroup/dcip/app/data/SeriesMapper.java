package com.capgroup.dcip.app.data;

import com.capgroup.dcip.domain.common.Schedule;
import com.capgroup.dcip.domain.data.Series;
import com.capgroup.dcip.domain.data.SeriesSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapping between a Series and a SeriesModel
 */
@Mapper
public interface SeriesMapper {
    SeriesModel map(Series series);

    Series map(SeriesModel model);

    Iterable<SeriesModel> mapAllSeries(Iterable<Series> data);

    @Mappings({@Mapping(target="cron", source="schedule.cron"),
            @Mapping(target="fixedRate", source="schedule.fixedRate"),
            @Mapping(target="fixedDelay", source="schedule.fixedDelay")})
    Schedule map(SeriesSchedule schedule);
}
