package com.capgroup.dcip.app.data;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.capgroup.dcip.infrastructure.repository.DataSourceConnectionRepository;

/**
 * Managers the TimeSeriesDataSourceConnection objects. On initialization
 * retrieves the DataSourceConnections from the database and uses the
 * TimeSeriesDataSourceConnection factory to create the appropriate
 * TimeSeriesDataSourceConnection objects. using the annotation @Transactional
 * does not work in PostConstruct - hence the use of the
 * transactionManager/Template
 */
@Component
@Slf4j
public class TimeSeriesDataSourceConnectionManagerImpl implements TimeSeriesDataSourceConnectionManager {
	private List<TimeSeriesDataSourceConnection> dataSourceConnections;
	private DataSourceConnectionRepository dataSourceConnectionRepository;
	private TimeSeriesDataSourceConnectionFactory timeSeriesDataSourceConnectionFactory;
	protected PlatformTransactionManager txManager;

	@Autowired
	public TimeSeriesDataSourceConnectionManagerImpl(DataSourceConnectionRepository dataSourceRepository,
			TimeSeriesDataSourceConnectionFactory timeSeriesDataSourceConnectionFactory,
			PlatformTransactionManager txManager) {
		this.dataSourceConnectionRepository = dataSourceRepository;
		this.timeSeriesDataSourceConnectionFactory = timeSeriesDataSourceConnectionFactory;
		this.txManager = txManager;
	}

	public Collection<TimeSeriesDataSourceConnection> dataSourceConnections() {
		return dataSourceConnections;
	}

	@PostConstruct
	public void init() {
		TransactionTemplate template = new TransactionTemplate(txManager);
		template.setReadOnly(true);
		template.execute(status -> {
			try {
				log.info("initialising datasource connections");
				dataSourceConnections = StreamSupport.stream(dataSourceConnectionRepository.findAll().spliterator(), false)
						.map(timeSeriesDataSourceConnectionFactory::create).collect(Collectors.toList());
				log.info("datasource connections successfully initialised");
				return null;

			} catch (Throwable thr){
				log.error("Error initializing Connection Manager", thr);
				throw thr;
			}
		});
	}
}
