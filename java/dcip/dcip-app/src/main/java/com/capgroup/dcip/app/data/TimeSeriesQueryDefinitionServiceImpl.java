package com.capgroup.dcip.app.data;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.event.application.DeletedApplicationEvent;
import com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews;
import com.capgroup.dcip.domain.data.DataSourceQueryDefinition;
import com.capgroup.dcip.domain.data.QDataSourceQueryDefinition;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.infrastructure.repository.DataSourceQueryDefinitionRepository;
import com.capgroup.dcip.infrastructure.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Application specific logic for processing/retrieving/writing
 * TimeSeriesQueryDefinition entities
 */
@Service
@EnableAllVirtualViews
public class TimeSeriesQueryDefinitionServiceImpl implements TimeSeriesQueryDefinitionService {

    DataSourceQueryDefinitionRepository dataSourceQueryDefinitionRepository;
    TimeSeriesQueryDefinitionMapper timeSeriesQueryDefinitionMapper;
    EntityManager entityManager;
    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public TimeSeriesQueryDefinitionServiceImpl(DataSourceQueryDefinitionRepository repository,
                                                TimeSeriesQueryDefinitionMapper mapper, EntityManager entityManager,
                                                ApplicationEventPublisher applicationEventPublisher) {
        this.dataSourceQueryDefinitionRepository = repository;
        this.timeSeriesQueryDefinitionMapper = mapper;
        this.entityManager = entityManager;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Transactional
    public TimeSeriesQueryDefinitionModel create(TimeSeriesQueryDefinitionModel model) {
        DataSourceQueryDefinition definition = timeSeriesQueryDefinitionMapper.map(model);
        definition = dataSourceQueryDefinitionRepository.save(definition);

        entityManager.flush();

        return timeSeriesQueryDefinitionMapper.map(definition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeSeriesQueryDefinitionModel> findAll(Long profileId) {
        EntityRepository.ExpressionBuilder expressionBuilder =
                new EntityRepository.ExpressionBuilder(QDataSourceQueryDefinition.dataSourceQueryDefinition._super._super);
        return timeSeriesQueryDefinitionMapper.map(this.dataSourceQueryDefinitionRepository
                .findAll(expressionBuilder.hasProfile(profileId)));
    }

    @Override
    @Transactional(readOnly = true)
    public TimeSeriesQueryDefinitionModel findById(long id) {
        return timeSeriesQueryDefinitionMapper.map(this.dataSourceQueryDefinitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DataSourceQueryDefinition", Long.toString(id))));
    }

    @Override
    @Transactional
    public TimeSeriesQueryDefinitionModel delete(long id) {
        return dataSourceQueryDefinitionRepository.findById(id).map(x -> {

            // publish the event
            TimeSeriesQueryDefinitionModel result = timeSeriesQueryDefinitionMapper.map(x);
            new DeletedApplicationEvent<>(result).onComplete(evnt ->
                    dataSourceQueryDefinitionRepository.delete(x)
            ).onReject(evnt -> x.setStatus(TemporalEntity.Status.MARKED_FOR_DELETE)).publish(applicationEventPublisher);

            return result;
        }).orElse(null);
    }

    @Override
    @Transactional
    public TimeSeriesQueryDefinitionModel update(long id, TimeSeriesQueryDefinitionModel model) {
        DataSourceQueryDefinition definition = this.dataSourceQueryDefinitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DataSourceQueryDefinition", Long.toString(id)));

        this.timeSeriesQueryDefinitionMapper.update(model, definition);
        definition = this.dataSourceQueryDefinitionRepository.save(definition);
        entityManager.flush();

        return this.timeSeriesQueryDefinitionMapper.map(definition);
    }

}
