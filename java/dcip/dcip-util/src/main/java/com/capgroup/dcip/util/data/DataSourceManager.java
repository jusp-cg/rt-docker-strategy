package com.capgroup.dcip.util.data;

import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataSourceManager {

	private DataSourceFactory dataSourceFactory;
	private ConcurrentHashMap<String, DataSource> dataSources = new ConcurrentHashMap<String, DataSource>();

	@Autowired
	public DataSourceManager(DataSourceFactory dataSourceFactory) {
		this.dataSourceFactory = dataSourceFactory;
	}

	public DataSource dataSource(String name) {
		return this.dataSources.computeIfAbsent(name, this::createDataSource);
	}

	protected DataSource createDataSource(String name) {
		long start = 0;
		if (log.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		DataSource result = dataSourceFactory.createDataSource(name);
		if (log.isDebugEnabled()) {
			log.debug("Time to create datasource: {}, took {}ms", name, System.currentTimeMillis() - start);
		}
		return result;
	}
}
