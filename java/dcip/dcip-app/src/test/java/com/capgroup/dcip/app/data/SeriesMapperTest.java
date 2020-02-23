package com.capgroup.dcip.app.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.MappersTestConfig;
import com.capgroup.dcip.domain.data.Series;

@ContextConfiguration(classes = MappersTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(SeriesMapperImpl.class)
public class SeriesMapperTest {

	@Autowired
	private SeriesMapper seriesMapper;

	@Test
	public void toSeriesTest() {
		SeriesModel model = new SeriesModel();
		model.setDescription("description");
		model.setId(123);
		model.setName("name");

		Series result = seriesMapper.map(model);
		assertEquals(123, (long)result.getId());
		assertEquals("name", result.getName());
		assertEquals("description", result.getDescription());
	}

	@Test
	public void toSerieModelTest() {
		Series series = new Series();
		series.setId(456l);
		series.setName("another name");
		series.setDescription("another description");

		SeriesModel result = seriesMapper.map(series);
		assertEquals(456, result.getId());
		assertEquals("another name", result.getName());
		assertEquals("another description", result.getDescription());
	}
}
