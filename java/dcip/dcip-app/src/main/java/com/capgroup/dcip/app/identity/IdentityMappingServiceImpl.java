package com.capgroup.dcip.app.identity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.capgroup.dcip.domain.data.DataSource;
import com.capgroup.dcip.domain.entity.Entity;
import com.capgroup.dcip.domain.entity.EntityType;
import com.capgroup.dcip.domain.identity.IdentityMapping;
import com.capgroup.dcip.infrastructure.repository.IdentityMappingRepository;
import com.capgroup.dcip.util.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
public class IdentityMappingServiceImpl implements IdentityMappingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IdentityMappingServiceImpl.class);

	/**
	 * key for quick lookup between internal/external mapEntity
	 */
	@Data
	@AllArgsConstructor
	private static class InternalKey {
		private long entityTypeId;
		private long internalId;
		private long dataSourceId;

		public static InternalKey create(IdentityMapping mapping) {
			return new InternalKey(mapping.getEntityTypeId(), mapping.getInternalId(), mapping.getDataSourceId());
		}
	}

	/**
	 * key for quick lookup between external/internal mapEntity
	 */
	@Data
	@AllArgsConstructor
	private static class ExternalKey {
		private long entityTypeId;
		private String externalId;
		private long dataSourceId;

		public static ExternalKey create(IdentityMapping mapping) {
			return new ExternalKey(mapping.getEntityTypeId(), mapping.getExternalId(), mapping.getDataSourceId());
		}
	}

	private IdentityMappingRepository identityMappingRepository;

	// mapping between internal common and external common
	private ConcurrentMap<InternalKey, IdentityMapping> internalToExternalMapping;

	// mapping between external common and internal ids
	private ConcurrentMap<ExternalKey, IdentityMapping> externalToInternalMapping;
	private PlatformTransactionManager platformTransactionManager;

	@Value("${identityMapping.preload}")
	private boolean preLoadIdentityMapping;

	@Autowired
	public IdentityMappingServiceImpl(IdentityMappingRepository repository,
			PlatformTransactionManager platformTransactionManager) {
		this.identityMappingRepository = repository;
		this.platformTransactionManager = platformTransactionManager;
	}

	@Override
	public OptionalLong internalIdentifier(String externalId, DataSource dataSource, EntityType entityType) {
		return internalIdentifier(externalId, dataSource.getId(), entityType.getId());
	}

	@Override
	public Optional<String> externalIdentifier(long internalId, DataSource dataSource, EntityType entityType) {
		return externalIdentifier(internalId, dataSource.getId(), entityType.getId());
	}

	@Override
	public Optional<String> externalIdentifier(Entity entity, DataSource dataSource) {
		return externalIdentifier(entity.getId(), dataSource, entity.getEntityType());
	}

	@PostConstruct
	public void init() {

		// only load in the cache if the flag is set to true
		if (!preLoadIdentityMapping) {
			this.externalToInternalMapping = new ConcurrentHashMap<>();
			this.internalToExternalMapping = new ConcurrentHashMap<>();
			return;
		}

		long startTime = System.nanoTime();

		TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
		transactionTemplate.setReadOnly(true);

		transactionTemplate.execute(status -> {
			Iterable<IdentityMapping> mappings = identityMappingRepository.findAll();

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Time to load in common mapping: " + (System.nanoTime() - startTime) / 1000000000 + "s");
			long loadingMapStartTime = System.nanoTime();

			this.externalToInternalMapping = StreamSupport.stream(mappings.spliterator(), true)
					.collect(Collectors.toConcurrentMap(ExternalKey::create, Function.identity()));
			this.internalToExternalMapping = StreamSupport.stream(mappings.spliterator(), true)
					.collect(Collectors.toConcurrentMap(InternalKey::create, Function.identity()));

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Time to cache identityMapping: " + (System.nanoTime() - loadingMapStartTime) / 1000000000
						+ "s");

			return null;
		});

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Time to init service:" + (System.nanoTime() - startTime) / 1000000000 + "s");
	}

	@Override
	public OptionalLong internalIdentifier(String externalId, long dataSourceId, long entityTypeId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Retrieving internal identifier for externalId:{}, dataSourceId:{}, entityTypeId:{} ",
					externalId, dataSourceId, entityTypeId);
		}

		IdentityMapping mapping = externalToInternalMapping
				.computeIfAbsent(new ExternalKey(entityTypeId, externalId, dataSourceId), x -> {
					return identityMappingRepository
							.findByExternalIdAndEntityTypeIdAndDataSourceId(externalId, entityTypeId, dataSourceId)
							.orElse(null);
				});
		return mapping == null ? OptionalLong.empty() : OptionalLong.of(mapping.getInternalId());
	}

	@Override
	public Optional<String> externalIdentifier(long internalId, long dataSourceId, long entityTypeId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Retrieving external identifier for internalId:{}, dataSourceId:{}, entityTypeId:{}",
					internalId, dataSourceId, entityTypeId);
		}		
		
		IdentityMapping mapping = internalToExternalMapping
				.computeIfAbsent(new InternalKey(entityTypeId, internalId, dataSourceId), key -> {
					return identityMappingRepository
							.findByInternalIdAndEntityTypeIdAndDataSourceId(internalId, entityTypeId, dataSourceId)
							.orElse(null);
				});
		return Optional.ofNullable(mapping == null ? null : mapping.getExternalId());
	}

	@Override
	public Optional<String> externalIdentifier(long internalId, DataSource dataSource, long entityTypeId) {
		return externalIdentifier(internalId, dataSource.getId(), entityTypeId);
	}

	@Override
	public Map<String, Long> internalIdentifiers(long dataSourceId, long entityTypeId, Iterable<String> externalIds) {

		List<String> inputIds = CollectionUtils.asList(externalIds);

		// lookup for existing mappings
		Map<String, IdentityMapping> existingMappings = inputIds.stream()
				.map(x -> externalToInternalMapping.get(new ExternalKey(entityTypeId, x, dataSourceId)))
				.filter(x -> x != null).collect(Collectors.toMap(x -> x.getExternalId(), Function.identity()));

		// findAll the external ids that haven't been matched
		List<String> unmatchedItems = inputIds.stream().filter(x -> !existingMappings.containsKey(x))
				.collect(Collectors.toList());

		if (!unmatchedItems.isEmpty()) {
			Iterable<IdentityMapping> retrievedFromDB = identityMappingRepository
					.findByEntityTypeIdAndDataSourceIdAndExternalIdIn(unmatchedItems, entityTypeId, dataSourceId);

			// add the ones retrieved from the DB to the cache
			retrievedFromDB.forEach(x -> {
				externalToInternalMapping.putIfAbsent(ExternalKey.create(x), x);
				internalToExternalMapping.putIfAbsent(InternalKey.create(x), x);
			});

			return Stream
					.concat(existingMappings.values().stream(),
							StreamSupport.stream(retrievedFromDB.spliterator(), false))
					.collect(Collectors.toMap(x -> x.getExternalId(), x -> x.getInternalId()));
		}

		return existingMappings.values().stream()
				.collect(Collectors.toMap(x -> x.getExternalId(), x -> x.getInternalId()));
	}

}
