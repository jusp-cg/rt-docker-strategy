package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.identity.QUser;
import com.capgroup.dcip.domain.identity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for CRUD operations on a User
 */
@Repository
public interface UserRepository extends TemporalEntityRepository<User>, QuerydslPredicateExecutor<User> {

    Optional<User> findByInitials(String initials);

    @EntityGraph(attributePaths = {"event"})
    @Query("select u from User u where (:initials = null OR lower(u.initials) = lower(cast(:initials as string))) AND (:applicationRoleId = null OR" +
            " EXISTS(select p.id from Profile p where p.user.id = u.id and p.applicationRole.id = :applicationRoleId))")
    Iterable<User> findAllByInitialsAndApplicationRole(@Param("initials") String initials,
                                                       @Param("applicationRoleId") Long applicationRoleId);

    /**
     * Builder class for creating DSL expressions
     */
    class ExpressionBuilder extends TemporalEntityRepository.ExpressionBuilder {

        QUser user;

        public ExpressionBuilder() {
            this(QUser.user);
        }

        public ExpressionBuilder(QUser user) {
            super(user._super);
            this.user = user;
        }

        public BooleanExpression hasUserInitials(String userInitials) {
            return userInitials == null ? Expressions.TRUE.eq(true) : user.initials.eq(userInitials);
        }
    }
}
