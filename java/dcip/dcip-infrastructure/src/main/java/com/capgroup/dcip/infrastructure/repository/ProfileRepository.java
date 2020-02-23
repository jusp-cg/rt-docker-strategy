package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.QProfile;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for CRUD operations on a Profile
 */
@Repository
public interface ProfileRepository extends TemporalEntityRepository<Profile>, QuerydslPredicateExecutor<Profile> {

    Iterable<Profile> findByUserId(long userId);

    Iterable<Profile> findAllByUserIdIn(List<Long> userIds);

    /**
     * True iff the {@code profileId} is associated with the {@code userInitials} or if the user has OnBehalf with
     * {@code profileId}
     */
    @Query("SELECT CASE WHEN count(u) > 0 THEN true ELSE false END FROM User u WHERE lower(u.initials) = lower(cast(:userInitials as string)) AND " +
            "EXISTS (SELECT 1 FROM Profile p where (p.user = u OR p.user IN (SELECT o.onBehalfOfUser FROM u.onBehalfOfUsers o)) AND p.id = :profileId)")
    boolean isProfileValidForUser(@Param("profileId") long profileId, @Param("userInitials") String userInitials);

    /**
     * Builder class for creating Profile DSL expressions
     */
    class ExpressionBuilder extends TemporalEntityRepository.ExpressionBuilder {

        QProfile profile;

        public ExpressionBuilder() {
            this(QProfile.profile);
        }

        public ExpressionBuilder(QProfile profile) {
            super(profile._super);
            this.profile = profile;
        }

        public BooleanExpression hasUserInitials(String userInitials) {
            return userInitials == null ? Expressions.TRUE.eq(true)
                    : profile.user.initials.equalsIgnoreCase(userInitials);
        }

        @Override
        public BooleanExpression hasInvestmentUnit(Long investmentUnitId) {
            return investmentUnitId == null ? Expressions.TRUE.eq(true) :
                    profile.user.investmentUnit.id.eq(investmentUnitId);
        }

        public BooleanExpression hasRole(String role) {
            return role == null ? Expressions.TRUE.eq(true)
                    : profile.role.name.equalsIgnoreCase(role);
        }

        public BooleanExpression hasUserId(Long userId) {
            return userId == null ? Expressions.TRUE.eq(true) :
                    profile.user.id.eq(userId);
        }

        public BooleanExpression hasDefault(boolean defaultFlag) {
            return profile.defaultFlag.eq(defaultFlag);
        }
    }
}
