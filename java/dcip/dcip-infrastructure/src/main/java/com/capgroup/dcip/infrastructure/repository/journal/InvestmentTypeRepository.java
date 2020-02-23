package com.capgroup.dcip.infrastructure.repository.journal;

import com.capgroup.dcip.domain.journal.InvestmentType;
import com.capgroup.dcip.infrastructure.repository.TemporalEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentTypeRepository extends TemporalEntityRepository<InvestmentType> {
	@Query("select u from InvestmentType u where (:userInitials is null OR lower(u.event.profile.user.initials) = lower(cast(:userInitials as string)))")
	Iterable<InvestmentType> findAllByUserInitials(@Param("userInitials") String userInitials);

	Iterable<InvestmentType> findAllByDefaultFlagIsTrue();

	@Query("select max(u.orderBy) from InvestmentReason u where lower(u.event.profile.user.initials) = lower(cast(:userInitials as string))")
	Integer maxOrderByForInitials(String userInitials);
}
