package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.data.Series;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeriesRepository extends CrudRepository<Series, Long> {
    Optional<Series> findById(long id);
}
