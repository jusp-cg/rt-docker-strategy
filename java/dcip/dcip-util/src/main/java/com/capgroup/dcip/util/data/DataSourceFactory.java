package com.capgroup.dcip.util.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class DataSourceFactory {
    private static final String PROPERTY_FORMAT = "%s.datasource.%s";

    private Environment environment;

    @Autowired
    public DataSourceFactory(Environment env) {
        this.environment = env;
    }

    public javax.sql.DataSource createDataSource(String propertyPrefix) {

        HikariConfig config = new HikariConfig();

        // username to log into the source
        config.setPassword(environment.getProperty(String.format(PROPERTY_FORMAT, propertyPrefix, "password")));
        config.setUsername(environment.getProperty(String.format(PROPERTY_FORMAT, propertyPrefix, "username")));
        config.setJdbcUrl(environment.getProperty(String.format(PROPERTY_FORMAT, propertyPrefix,
                "url")));
        config.setDriverClassName(environment
                .getProperty(String.format(PROPERTY_FORMAT, propertyPrefix, "driver-class-name"),
                        String.class));
        String initSql = environment.getProperty(String.format(PROPERTY_FORMAT, propertyPrefix, "hikari.connection-init-sql"));

        if (StringUtils.hasText(initSql)){
            config.setConnectionInitSql(initSql);
        }
        return new HikariDataSource(config);
    }
}
