package com.capgroup.dcip.app.reference.listing;

import static com.capgroup.dcip.util.Lazy.lazy;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.capgroup.dcip.capitalconnect.company.CapitalConnectCompanyService;
import com.capgroup.dcip.capitalconnect.company.Company;
import com.capgroup.dcip.capitalconnect.suggestion.CapitalConnectSuggestionService;
import com.capgroup.dcip.capitalconnect.suggestion.MultiSuggestionContext;
import com.capgroup.dcip.capitalconnect.suggestion.Scope;

/**
 * Wrapper class that adapts the interface ListingService to the Capital Connect service.
 * The class is multi-threaded to restrict/control the number of threads that access the capital connect API 
 *
 */
@Service
public class CapitalConnectListingService implements ListingService {
	private static Logger LOGGER = LoggerFactory.getLogger(CapitalConnectListingService.class);

	@Value("${capital-connect.listing.concurrent-threads}")
	private int concurrency = 3;

	private CapitalConnectSuggestionService capitalConnectSuggestionService;
	private ListingFinder listingFinder;
	private CapitalConnectToListingMapper mapper;
	private Supplier<ForkJoinPool> threadPool = lazy(() -> new ForkJoinPool(concurrency));

	public interface ListingFinder {
		Optional<ListingModel> findById(String id);
	}

	/**
	 * This class is required for the Cacheable annotation
	 */
	@Component
	public static class ListingFinderImpl implements ListingFinder {
		CapitalConnectCompanyService capitalConnectCompanyService;
		CapitalConnectToListingMapper mapper;

		@Autowired
		public ListingFinderImpl(CapitalConnectCompanyService companyService, CapitalConnectToListingMapper mapper) {
			this.capitalConnectCompanyService = companyService;
			this.mapper = mapper;
		}

		@Cacheable("Listing")
		public Optional<ListingModel> findById(String id) {
			Company company = capitalConnectCompanyService.find(id);
			return Optional.ofNullable(mapper.toListingModel(company));
		}
	}

	@Autowired
	public CapitalConnectListingService(CapitalConnectSuggestionService suggestionService, ListingFinder listingFinder,
			CapitalConnectToListingMapper mapper) {
		capitalConnectSuggestionService = suggestionService;
		this.listingFinder = listingFinder;
		this.mapper = mapper;
	}

	@Override
	public Stream<ListingModel> find(String matches) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requesting companies that match:" + matches);
		}
		
		// filter out those items whose ids aren't all ints
		return mapper
				.toListingModels(capitalConnectSuggestionService
						.search(new MultiSuggestionContext(matches, new Scope("company"))))
				.filter(item -> item.getId().chars().allMatch(x -> Character.isDigit((char) x)));
	}

	@Override
	public Optional<ListingModel> findById(String id) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requesting company with id:" + id);
		}
		
		return listingFinder.findById(id);
	}

	@Override
	public Stream<ListingModel> findByIds(Stream<String> ids) {
		try {
			return threadPool.get().submit(() -> ids.parallel().map(x -> {
				try {
					return findById(x).orElse(ListingModel.CreateUnknown(x));
				} catch (Throwable thr) {
					LOGGER.error("Retrieving listing:" + x);
					return ListingModel.CreateError(x);
				}
			})).get();

		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Retrieving Listings:" + ids.toString());
			return ids.map(ListingModel::CreateError);
		}
	}
}