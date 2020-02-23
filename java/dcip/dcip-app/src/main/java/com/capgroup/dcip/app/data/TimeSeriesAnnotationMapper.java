package com.capgroup.dcip.app.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.mapstruct.AfterMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.annotation.Annotation;

/**
 * Responsible for converting between an Annotation and an AnnotationModel (and
 * vice-versa)
 */
@Mapper(config = TemporalEntityMapper.class)
public abstract class TimeSeriesAnnotationMapper {
	private static final long TIME_STAMP_PROPERTY_TYPE_ID = 1L;
	private static final long VALUE_PROPERTY_TYPE_ID = 2L;

	private static final long ANNOTATION_TYPE_ID = 1L;

	public abstract TimeSeriesAnnotationModel map(Annotation annotation);

	public abstract Annotation map(TimeSeriesAnnotationModel model);

	public abstract void update(TimeSeriesAnnotationModel model, @MappingTarget Annotation annotation);

	public abstract Stream<TimeSeriesAnnotationModel> mapAnnotation(Stream<Annotation> annotations);

	@InheritConfiguration(name="map")
	public abstract Stream<Annotation> mapModel(Stream<TimeSeriesAnnotationModel> models);

	public Stream<Annotation> mapModel(long entityId, Stream<TimeSeriesAnnotationModel> models) {
		return mapModel(models).map(x -> {
			x.setEntityId(entityId);
			return x;
		});
	}

	@AfterMapping
	protected void afterMap(TimeSeriesAnnotationModel model, @MappingTarget Annotation annotation) { 
		annotation.setProperty(TIME_STAMP_PROPERTY_TYPE_ID, model.getTimeStamp());
		annotation.setProperty(VALUE_PROPERTY_TYPE_ID, model.getValue());
		annotation.setAnnotationTypeId(ANNOTATION_TYPE_ID);
	}

	@AfterMapping
	protected void afterMap(Annotation annotation, @MappingTarget TimeSeriesAnnotationModel model) {
		Optional<LocalDateTime> optionalLocalDateTime = annotation.getProperty(TIME_STAMP_PROPERTY_TYPE_ID, LocalDateTime.class);
		optionalLocalDateTime.ifPresent(model::setTimeStamp);

		Optional<BigDecimal> optionalBigDecimal = annotation.getProperty(VALUE_PROPERTY_TYPE_ID, BigDecimal.class);
		optionalBigDecimal.ifPresent(model::setValue);
	}
}
