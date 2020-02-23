package com.capgroup.dcip.infrastructure.repository.journal;

import com.capgroup.dcip.domain.journal.InvestmentReason;
import com.capgroup.dcip.infrastructure.repository.TemporalEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentReasonRepository extends TemporalEntityRepository<InvestmentReason> {
    @Query("select u from InvestmentReason u where (:userInitials is null OR lower(event.profile.user.initials) = " +
            "lower(cast(:userInitials as string)))")
    Iterable<InvestmentReason> findAllByUserInitials(@Param("userInitials") String userInitials);

    Iterable<InvestmentReason> findAllByDefaultFlagIsTrue();

    @Query("select max(u.orderBy) from InvestmentReason u where lower(u.event.profile.user.initials) = lower(cast(:userInitials as string))")
    Integer maxOrderByForInitials(String userInitials);
}
