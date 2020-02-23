package com.capgroup.dcip.domain.data;
/*
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.capgroup.dcip.app.reference.company.CompanyModel;
import com.capgroup.dcip.util.LocalDateTimeRange;

@RunWith(SpringRunner.class)
public class DataSourceConnectionTest {

	DataSourceConnection connection;
	Series series;
	SeriesMapping seriesMapping;
	DataSource dataSource;

	@Before
	public void init() {
		dataSource = new DataSource(234);
		connection = new DataSourceConnection(1, dataSource);
		series = new Series(1l);
		seriesMapping = new SeriesMapping(series);

		connection.addSeries(seriesMapping);
		DataProperty property = new DataProperty();
		property.setKey("test");
		property.setValue("XXX");
		seriesMapping.getProperties().add(property);
	}

	@Test
	public void propertyReplacementTest() {
		TimeSeriesQuery query = new TimeSeriesQuery();
		query.setDateRange(new LocalDateTimeRange(LocalDateTimeRange.MIN_START, LocalDateTimeRange.MAX_END));
		query.setSeries(series);
		CompanyModel model = new CompanyModel();
		model.setId(12);
		query.setCompany(model);
		connection.setQueryString(
				"#{dateRange.startDate} #{dateRange.endDate} #{series.Id} #{company.id} #{properties['test']}");

		String result = connection.createTimeSeriesQuery(query);
		Assert.assertEquals(result, "1900-01-01 9999-12-31 1 12 XXX");
	}
}
*/