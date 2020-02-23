package com.capgroup.dcip.app.data;

import java.util.Optional;
import java.util.List;

import com.capgroup.dcip.domain.data.Series;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.app.reference.company.CompanyService;
import com.capgroup.dcip.domain.data.DataSourceQueryDefinition;
import com.capgroup.dcip.infrastructure.repository.SeriesRepository;

/**
 * Mapping/transformation between the domain object DataSourceQueryDefinition
 * and TiemSeriesQueryDefinitionModel
 */
@Mapper(config = TemporalEntityMapper.class, uses = SeriesMapper.class)
public abstract class TimeSeriesQueryDefinitionMapper {
	@Autowired
	CompanyService companyService;

	@Autowired
	SeriesRepository seriesRepository;

	public abstract TimeSeriesQueryDefinitionModel map(DataSourceQueryDefinition dataSourceQuery);

	@Mappings({ @Mapping(source = "company.id", target = "companyId") })
	public abstract DataSourceQueryDefinition map(TimeSeriesQueryDefinitionModel model);

	public abstract List<TimeSeriesQueryDefinitionModel> map(Iterable<DataSourceQueryDefinition> dataSourceQueries);

	@InheritConfiguration(name="map")
	public abstract void update(TimeSeriesQueryDefinitionModel model,
			@MappingTarget DataSourceQueryDefinition definition);

	@AfterMapping
	protected void afterMap(DataSourceQueryDefinition definition, @MappingTarget TimeSeriesQueryDefinitionModel model) {
		model.setCompany(CompanyService.findByIdOrUnknown(companyService, definition.getCompanyId()));
	}

	@AfterMapping
	protected void afterMap(TimeSeriesQueryDefinitionModel model, @MappingTarget DataSourceQueryDefinition definition) {
		Optional<Series> seriesOptional = seriesRepository.findById(model.getSeries().getId());
		seriesOptional.ifPresent(definition::setSeries);
	}
}
