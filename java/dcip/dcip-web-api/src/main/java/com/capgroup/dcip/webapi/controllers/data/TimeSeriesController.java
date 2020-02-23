package com.capgroup.dcip.webapi.controllers.data;

import com.capgroup.dcip.app.data.*;
import com.capgroup.dcip.domain.data.AuditableTimeSeries;
import com.capgroup.dcip.domain.data.TimeSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * External API for querying TimeSeries and checking for audits
 */
@RestController
@RequestMapping("api/dcip/data/timeseries")
public class TimeSeriesController {
    private TimeSeriesService timeSeriesService;

    @Autowired
    public TimeSeriesController(TimeSeriesService svc) {
        this.timeSeriesService = svc;
    }

    @PostMapping
    public Iterable<TimeSeriesQueryResultModel<TimeSeries.Entry>> post(@RequestBody List<TimeSeriesQueryModel> request) {
        return timeSeriesService.queryAll(request);
    }

    @PatchMapping
    public void patch(@RequestBody List<TimeSeriesUpdateModel> updates) {
        timeSeriesService.update(updates);
    }

    @PostMapping("/delete")
    public void postDelete(@RequestBody List<TimeSeriesQueryModel> deletes) {
        timeSeriesService.deleteAll(deletes);
    }

//    @DeleteMapping("/{companyId}/{seriesId}//{userId}/{startDate}/{endDate}")
//    public TimeSeriesModel<TimeSeries.Entry> delete(@PathVariable("companyId") long companyId,
//                                                    @PathVariable("seriesId") long seriesId,
//                                                    @PathVariable("userId") Long userId,
//                                                    @PathVariable("startDate") @DateTimeFormat(iso =
//                                                            DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
//                                                    @PathVariable("endDate") @DateTimeFormat(iso =
//                                                            DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
//        return timeSeriesService.delete(new TimeSeriesQueryModel(companyId, seriesId, userId,
//                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startDate),
//                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endDate), null));
//    }

    @DeleteMapping("/{companyId}/{seriesId}//{userId}/{startDate}/{endDate}")
    public TimeSeriesModel<TimeSeries.Entry> delete(@PathVariable("companyId") long companyId,
                                                    @PathVariable("seriesId") long seriesId,
                                                    @PathVariable("userId") Long userId,
                                                    @PathVariable("startDate") String startDate,
                                                    @PathVariable("endDate") String endDate) {

                return timeSeriesService.delete(new TimeSeriesQueryModel(companyId, seriesId, userId,
                      startDate, endDate, null));
    }


    @GetMapping("/{companyId}/{seriesId}")
    public TimeSeriesModel<AuditableTimeSeries.AuditableEntry> get(@PathVariable("companyId") long companyId,
                                                                   @PathVariable("seriesId") long seriesId,
                                                                   @RequestParam("modifiedFrom") @DateTimeFormat(iso =
                                                                           DateTimeFormat.ISO.DATE_TIME) ZonedDateTime modifiedFrom,
                                                                   @RequestParam("modifiedTo") @DateTimeFormat(iso =
                                                                           DateTimeFormat.ISO.DATE_TIME) ZonedDateTime modifiedTo) {
        return timeSeriesService.audit(new TimeSeriesQueryModel(companyId, seriesId,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(modifiedFrom),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(modifiedTo), null));
    }

    @GetMapping("/{companyId}/{seriesId}/{startDate}/{endDate}")
    public TimeSeriesModel<TimeSeries.Entry> get(@PathVariable("companyId") long companyId,
                                                            @PathVariable("seriesId") long seriesId,
                                                            @PathVariable("startDate") String startDate,
                                                            @PathVariable(value = "endDate", required =
                                                                    false) String endDate,
                                                            @RequestParam(value = "readOffset", required =
                                                                    false) Integer readOffset) {
        return timeSeriesService.query(new TimeSeriesQueryModel(companyId, seriesId,
                startDate, endDate, readOffset));
    }

//    public static Date getSomeDate(final String str) throws ParseException {
//        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        sdf.setTimeZone(TimeZone.getTimeZone(("UTC")));
//        return sdf.parse(str);
//    }
}
