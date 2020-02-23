package com.capgroup.dcip.app.journal;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.domain.journal.InvestmentReason;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentReasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class InvestmentReasonServiceImpl implements InvestmentReasonService {

    InvestmentReasonRepository investmentReasonRepository;
    InvestmentReasonMapper investmentReasonMapper;
    EntityManager entityManager;
    RequestContextService requestContextService;

    @Autowired
    public InvestmentReasonServiceImpl(InvestmentReasonRepository repository,
                                       InvestmentReasonMapper investmentReasonMapper,
                                       EntityManager entityManager,
                                       RequestContextService requestContextService) {
        this.investmentReasonRepository = repository;
        this.investmentReasonMapper = investmentReasonMapper;
        this.entityManager = entityManager;
        this.requestContextService = requestContextService;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<InvestmentReasonModel> findByUserInitials(String userInitials, Boolean includePublic) {
        return investmentReasonMapper
                .map(
                        Stream.concat(StreamSupport.stream(investmentReasonRepository.findAllByUserInitials(userInitials).spliterator(), false),
                                Optional.ofNullable(includePublic).map(i -> i ?
                                        StreamSupport.stream
                                                (investmentReasonRepository.findAllByDefaultFlagIsTrue().spliterator(), false)
                                        : Stream.<InvestmentReason>empty()).orElse(Stream.empty()))
                                .sorted().collect(Collectors.toList())
                );
    }

    @Override
    @Transactional
    public InvestmentReasonModel create(InvestmentReasonModel investmentReasonModel) {
        InvestmentReason investmentReason = investmentReasonMapper.map(investmentReasonModel);
        Integer order =
                investmentReasonRepository.maxOrderByForInitials(requestContextService.currentProfile().getUser().getInitials());
        investmentReason.setOrderBy(order == null ? 0 : order);

        investmentReason = investmentReasonRepository.save(investmentReason);

        entityManager.flush();

        return this.investmentReasonMapper.map(investmentReason);
    }

    @Override
    @Transactional
    public InvestmentReasonModel update(long id, InvestmentReasonModel model) {
        InvestmentReason investmentReason = investmentReasonRepository.findById(id).orElseThrow(() ->
                // need to throw an exception so nothing is committed
                new ResourceNotFoundException("InvestmentReason", Long.toString(id)));
        investmentReasonMapper.update(model, investmentReason);
        investmentReason = investmentReasonRepository.save(investmentReason);

        // flush the result to the DB
        entityManager.flush();

        return investmentReasonMapper.map(investmentReason);
    }

    @Override
    @Transactional
    public InvestmentReasonModel delete(long id) {
        return investmentReasonRepository.findById(id).map(investmentReason ->
        {
            investmentReasonRepository.delete(investmentReason);
            return investmentReasonMapper.map(investmentReason);
        }).orElse(null);
    }
}