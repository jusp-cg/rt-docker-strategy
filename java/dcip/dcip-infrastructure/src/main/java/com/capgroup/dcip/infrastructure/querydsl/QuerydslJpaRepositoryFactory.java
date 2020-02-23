package com.capgroup.dcip.infrastructure.querydsl;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Creates the QueryDslJpaRepositoryImpl fragment for handling the Query executor API
 */
public class QuerydslJpaRepositoryFactory extends JpaRepositoryFactory {

    private final EntityManager entityManager;

    public QuerydslJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
    }

    @Override
    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
        RepositoryComposition.RepositoryFragments fragments = super.getRepositoryFragments(metadata);

        if (QuerydslPredicateProjectionExecutor.class.isAssignableFrom(
                metadata.getRepositoryInterface())) {

            JpaEntityInformation<?, Serializable> entityInformation =
                    getEntityInformation(metadata.getDomainType());

            Object queryableFragment = getTargetRepositoryViaReflection(
                    QuerydslJpaRepositoryImpl.class, entityInformation, entityManager);

            fragments = fragments.append(RepositoryFragment.implemented(queryableFragment));
        }

        return fragments;
    }
}
