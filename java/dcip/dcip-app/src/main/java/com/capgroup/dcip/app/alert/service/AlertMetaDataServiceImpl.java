package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.app.alert.AlertTypeModel;
import com.capgroup.dcip.app.alert.model.AlertActionTypeModel;
import com.capgroup.dcip.app.alert.model.AlertMetaDataMapper;
import com.capgroup.dcip.app.alert.service.AlertMetaDataService;
import com.capgroup.dcip.domain.alert.AlertType;
import com.capgroup.dcip.infrastructure.repository.alert.AlertActionTypeRepository;
import com.capgroup.dcip.infrastructure.repository.alert.AlertTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AlertMetaDataServiceImpl implements AlertMetaDataService {
    @Override
    @Transactional(readOnly = true)
    public Iterable<AlertActionTypeModel> findAllAlertActionTypes() {
        return alertMetaDataMapper.mapAllAlertActionTypes(alertActionTypeRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<AlertActionTypeModel> findAlertActionTypes(long id) {
        return null;
    }

    AlertTypeRepository alertDefinitionRepository;
    AlertActionTypeRepository alertActionTypeRepository;
    AlertMetaDataMapper alertMetaDataMapper;

    @Autowired
    public AlertMetaDataServiceImpl(AlertTypeRepository alertDefinitionRepository,
                                    AlertMetaDataMapper alertMetaDataMapper,
                                    AlertActionTypeRepository alertActionTypeRepository) {
        this.alertDefinitionRepository = alertDefinitionRepository;
        this.alertMetaDataMapper = alertMetaDataMapper;
        this.alertActionTypeRepository = alertActionTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<AlertTypeModel> findAllAlertTypes() {
        return alertMetaDataMapper.mapAllAlertTypes(sort(alertDefinitionRepository.findAll()));
    }

    @Override
    @Transactional(readOnly = true)
    public AlertTypeModel findAlertTypeById(long id) {
        return alertMetaDataMapper.map(alertDefinitionRepository.findById(id).get());
    }

    private Iterable<AlertType> sort(Iterable<AlertType> input) {
        return StreamSupport.stream(input.spliterator(), false).sorted().collect(Collectors.toList());
    }
}
