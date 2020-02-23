package com.capgroup.dcip.infrastructure.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capgroup.dcip.domain.identity.InvestmentUnit;

@Repository
public interface InvestmentUnitRepository extends CrudRepository<InvestmentUnit, Long>{

}
