package com.capgroup.dcip.app.data;

import com.capgroup.dcip.domain.data.DataSourceConnection;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Responsible for creating the appropriate TimeSeriesDataSourceConnection given
 * a DataSourceConnection
 */
@Component
public class TimeSeriesDataSourceConnectionFactoryImpl implements TimeSeriesDataSourceConnectionFactory {

    BeanFactory beanFactory;

    @Autowired
    public TimeSeriesDataSourceConnectionFactoryImpl(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public TimeSeriesDataSourceConnection create(DataSourceConnection connection) {
        return (TimeSeriesDataSourceConnection) beanFactory.getBean(connection.getConnectionType(), connection);
    }
}
