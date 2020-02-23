package com.capgroup.dcip.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * Streams items from the database. Closes the resultset/statement/connection
 * when the resulting stream is closed
 */
public class JdbcStreamTemplate extends JdbcTemplate {
	public JdbcStreamTemplate(DataSource dataSource) {
		super(dataSource);
	}

	public <T> Stream<T> streamQuery(String sql, ResultSetExtractor<T> extractor) {
		return new StreamQuery().stream(sql, extractor);
	}
	
	public <T> Stream<ResultSet> streamQuery(String sql){
		return new StreamQuery().stream(sql, (resultSet)->resultSet);
	}

	class StreamQuery {
		private Connection connection;
		private Statement statement;
		private ResultSet resultSet;

		public <T> Stream<T> stream(String sql, ResultSetExtractor<T> extractor) {

			try {
				connection = DataSourceUtils.getConnection(getDataSource());

				// forward only and min fetch size will retrieve one row at a time (i.e. stream
				// the data)
				// this needs to be performance tested. Will work well for large datasets but
				// slow for small ones
				statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				//statement.setFetchSize(Integer.MIN_VALUE);

				resultSet = statement.executeQuery(sql);

				Stream<T> result = StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE,
						Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE) {
					@Override
					public boolean tryAdvance(Consumer<? super T> action) {
						try {
							if (!resultSet.next()) {
								return false;
							}
							action.accept(extractor.extractData(resultSet));
							return true;
						} catch (SQLException e) {
							throw new RuntimeException(e);
						}
					}
				}, false).onClose(() -> close());

				return result;

			} catch (SQLException exc) {
				throw new RuntimeException(exc);
			} finally {
				close();
			}
		}

		public void close() {
			JdbcUtils.closeResultSet(resultSet);
			JdbcUtils.closeStatement(statement);
			try {
				DataSourceUtils.doCloseConnection(connection, getDataSource());
			} catch (SQLException exc) {
			}
		}
	}
}