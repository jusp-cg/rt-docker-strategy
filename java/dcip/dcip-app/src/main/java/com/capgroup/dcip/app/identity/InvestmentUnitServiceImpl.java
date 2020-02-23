package com.capgroup.dcip.app.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.infrastructure.repository.InvestmentUnitRepository;

@Service
public class InvestmentUnitServiceImpl implements InvestmentUnitService {

	InvestmentUnitRepository investmentUnitRepository;
	InvestmentUnitMapper investmentUnitMapper;

	@Autowired
	public InvestmentUnitServiceImpl(InvestmentUnitRepository repository, InvestmentUnitMapper investmentUnitMapper) {
		this.investmentUnitRepository = repository;
		this.investmentUnitMapper = investmentUnitMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<InvestmentUnitModel> findAll() {
		return investmentUnitMapper.map(investmentUnitRepository.findAll());
	}

	@Override
	@Transactional(readOnly = true)
	public InvestmentUnitModel findById(long id) {
		return investmentUnitRepository.findById(id).map(investmentUnitMapper::map)
				.orElseThrow(() -> new ResourceNotFoundException("InvestmentUnit", Long.toString(id)));
	}
}
 