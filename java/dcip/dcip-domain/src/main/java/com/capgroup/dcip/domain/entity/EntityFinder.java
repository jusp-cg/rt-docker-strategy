package com.capgroup.dcip.domain.entity;

import java.util.Optional;

public interface EntityFinder<T> {
	Optional<T> findById(long id);

	Iterable<T> findAll();
}
