package com.capgroup.dcip.app.journal;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.domain.journal.InvestmentType;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class InvestmentTypeServiceImpl implements InvestmentTypeService {

    InvestmentTypeRepository investmentTypeRepository;
    InvestmentTypeMapper investmentTypeMapper;
    EntityManager entityManager;
    RequestContextService requestContextService;

    @Autowired
    public InvestmentTypeServiceImpl(InvestmentTypeRepository repository, InvestmentTypeMapper investmentTypeMapper,
                                     EntityManager entityManager,
                                     RequestContextService requestContextService) {
        this.investmentTypeRepository = repository;
        this.investmentTypeMapper = investmentTypeMapper;
        this.entityManager = entityManager;
        this.requestContextService = requestContextService;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<InvestmentTypeModel> findByUserInitials(String userInitials, Boolean includePublic) {
        return investmentTypeMapper
                .mapAll(Stream.concat(StreamSupport.stream(investmentTypeRepository.findAllByUserInitials(userInitials).spliterator(), false),
                        Optional.ofNullable(includePublic).map(i -> i ?
                                StreamSupport.stream
                                        (investmentTypeRepository.findAllByDefaultFlagIsTrue().spliterator(), false)
                                : Stream.<InvestmentType>empty()).orElse(Stream.empty()))
                        .sorted().collect(Collectors.toList())
                );
    }

    @Override
    @Transactional
    public InvestmentTypeModel create(InvestmentTypeModel investmentTypeModel) {
        InvestmentType investmentType = investmentTypeMapper.map(investmentTypeModel);
        Integer order =
                investmentTypeRepository.maxOrderByForInitials(requestContextService.currentProfile().getUser().getInitials());
        investmentType.setOrderBy(order == null ? 0 : order);
        InvestmentType result = investmentTypeRepository.save(investmentType);

        entityManager.flush();

        return this.investmentTypeMapper.map(result);
    }

    @Override
    @Transactional
    public InvestmentTypeModel update(long id, InvestmentTypeModel model) {
        InvestmentType investmentType = investmentTypeRepository.findById(id).orElseThrow(() ->
                // need to throw an exception so nothing is committed
                new ResourceNotFoundException("InvestmentType", Long.toString(id)));
        investmentTypeMapper.update(model, investmentType);
        InvestmentType result = investmentTypeRepository.save(investmentType);

        // flush the result to the DB
        entityManager.flush();

        return investmentTypeMapper.map(result);
    }

    @Override
    @Transactional
    public InvestmentTypeModel delete(long id) {
        return investmentTypeRepository.findById(id).map(investmentType ->
        {
            investmentTypeRepository.delete(investmentType);
            return investmentTypeMapper.map(investmentType);
        }).orElse(null);
    }
}
