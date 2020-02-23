package com.capgroup.dcip.app.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.MappersTestConfig;
import com.capgroup.dcip.domain.annotation.Annotation;
import com.capgroup.dcip.util.ConverterUtils;
import com.capgroup.dcip.util.DateUtil;

@ContextConfiguration(classes = MappersTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import({ TimeSeriesAnnotationMapperImpl.class, DefaultFormattingConversionService.class, ConverterUtils.class })
public class TimeSeriesAnnotationMapperTest {

	@Autowired
	TimeSeriesAnnotationMapper annotationMapper;
	
	@MockBean
	ConversionService conversionService;
	
	LocalDateTime now;
	BigDecimal value = BigDecimal.valueOf(1234d);
	
	@Before
	public void testInit() {
		now = LocalDateTime.now();
		given(conversionService.convert(now,  String.class)).willReturn(DateUtil.format(now));
		given(conversionService.convert(DateUtil.format(now),  LocalDateTime.class)).willReturn(now);
		given(conversionService.convert(value,  String.class)).willReturn(value.toString());
		given(conversionService.convert(value.toString(),  BigDecimal.class)).willReturn(value);
	}
	
	@Test
	public void toAnnotationTest() {
		TimeSeriesAnnotationModel model = new TimeSeriesAnnotationModel();
		model.setEntityId(12);
		model.setProfileId(234);
		model.setText("text");
		model.setValue(value);
		model.setTimeStamp(now);
		
		Annotation annotation = annotationMapper.map(model);

		assertEquals(12, annotation.getEntityId());
		assertEquals("text", annotation.getText());
		assertTrue(annotation.getProperties().stream().filter(x -> x.getAnnotationPropertyTypeId() == 1).findFirst()
				.get().getValue().equals(ConverterUtils.convertTo(now, String.class)));
		assertTrue(annotation.getProperties().stream().filter(x -> x.getAnnotationPropertyTypeId() == 2).findFirst()
				.get().getValue().equals(ConverterUtils.convertTo(value, String.class)));
	}

	@Test
	public void toAnnotationModelTest() {
		Annotation annotation = new Annotation();
		annotation.setProperty(2, value);
		annotation.setProperty(1, now);
		annotation.setAnnotationTypeId(1);
		annotation.setEntityId(3454);
		annotation.setText("text");
		
		TimeSeriesAnnotationModel model = annotationMapper.map(annotation);
		
		assertEquals(value, model.getValue());
		assertEquals(now, model.getTimeStamp());
		assertEquals(3454, model.getEntityId());
		assertEquals("text", model.getText());
	}

	@Test
	public void updateAnnotationModelTest() {
		Annotation annotation = new Annotation();
		annotation.setProperty(2, value);
		annotation.setProperty(1, now);
		annotation.setAnnotationTypeId(1);
		annotation.setEntityId(3454);
		annotation.setText("text");

		TimeSeriesAnnotationModel model = new TimeSeriesAnnotationModel();
		model.setEntityId(12);
		model.setProfileId(234);
		model.setText("text");
		model.setValue(value);
		model.setTimeStamp(now);
		
		annotationMapper.update(model, annotation);

		assertEquals(12, annotation.getEntityId());
		assertEquals("text", annotation.getText());
		assertTrue(annotation.getProperties().stream().filter(x -> x.getAnnotationPropertyTypeId() == 1).findFirst()
				.get().getValue().equals(ConverterUtils.convertTo(now, String.class)));
		assertTrue(annotation.getProperties().stream().filter(x -> x.getAnnotationPropertyTypeId() == 2).findFirst()
				.get().getValue().equals(ConverterUtils.convertTo(value, String.class)));
	}
}
