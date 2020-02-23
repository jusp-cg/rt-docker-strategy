package com.capgroup.dcip.app.reference.listing;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Interface to abstract where listings are obtained from 
 */
public interface ListingService {
	Stream<ListingModel> find(String matches);

	Optional<ListingModel> findById(String id);

	Stream<ListingModel> findByIds(Stream<String> ids);
}
