package com.capgroup.dcip.app.journal;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.domain.journal.InvestmentActionType;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentActionTypeRepository;

@Service
public class InvestmentActionTypeServiceImpl implements InvestmentActionTypeService {

	private InvestmentActionTypeRepository investmentActionTypeRepository;
	private InvestmentActionTypeMapper investmentActionTypeMapper;
	private EntityManager entityManager;

	@Autowired
	public InvestmentActionTypeServiceImpl(InvestmentActionTypeRepository investmentActionTypeRepository,
			InvestmentActionTypeMapper investmentActionTypeMapper, EntityManager entityManager) {
		this.investmentActionTypeRepository = investmentActionTypeRepository;
		this.investmentActionTypeMapper = investmentActionTypeMapper;
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = true)
	public Stream<InvestmentActionTypeModel> findByProfileId(Long profileId) {
		return investmentActionTypeMapper.mapAll(investmentActionTypeRepository
				.findByEventProfileId(profileId).stream().sorted(Comparator.comparing(InvestmentActionType::getOrderBy)))
				.collect(Collectors.toList()).stream();
	}

	@Override
	@Transactional(readOnly = true)
	public Stream<InvestmentActionTypeModel> findAll() {
		return investmentActionTypeMapper
				.mapAll(
						StreamSupport.stream(investmentActionTypeRepository.findAll().spliterator(), false)
								.sorted(Comparator.comparing((InvestmentActionType x) -> x.getGroup().getOrderBy())
                                        .thenComparing(InvestmentActionType::getOrderBy)))
				.collect(Collectors.toList()).stream();
	}

	@Override
	@Transactional(readOnly = true)
	public InvestmentActionTypeModel findById(long id) {
		return investmentActionTypeRepository.findById(id).map(investmentActionTypeMapper::map)
				.orElse(null);
	}

	@Override
	@Transactional
	public InvestmentActionTypeModel create(InvestmentActionTypeModel investmentActionTypeModel) {
		InvestmentActionType investmentActionType = investmentActionTypeMapper
				.map(investmentActionTypeModel);

		InvestmentActionType result = investmentActionTypeRepository.save(investmentActionType);

		// Synchronize the persistence context to the underlying database.
		entityManager.flush();

		return investmentActionTypeMapper.map(result);
	}

	@Override
	@Transactional
	public InvestmentActionTypeModel update(long id, InvestmentActionTypeModel investmentActionTypeModel) {
		InvestmentActionType investmentActionType = investmentActionTypeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("InvestmentAction", Long.toString(id)));

		investmentActionTypeMapper.updateInvestmentActionType(investmentActionTypeModel, investmentActionType);
		InvestmentActionType result = investmentActionTypeRepository.save(investmentActionType);

		// Synchronize the persistence context to the underlying database.
		entityManager.flush();

		return investmentActionTypeMapper.map(result);
	}

	@Override
	@Transactional
	public void delete(long id) {
		InvestmentActionType investmentActionType = investmentActionTypeRepository.findById(id).orElseThrow(() ->
		// need to throw an exception so nothing is committed
		new ResourceNotFoundException("InvestmentActionType", Long.toString(id)));

		investmentActionTypeRepository.delete(investmentActionType);
	}
}
