package com.ifc.populationorderdeterminant.providers;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

public class JdbcProvider {

    private static DataSource dataSource;
    private static JdbcTemplate jdbcTemplate;

    private static final String HOST = PropertiesProvider.getRequiredProperty("db.host");
    private static final String PORT = PropertiesProvider.getRequiredProperty("db.port");
    private static final String DB_NAME = PropertiesProvider.getRequiredProperty("db.name");
    private static final String DB_USER = PropertiesProvider.getRequiredProperty("db.user");
    private static final String DB_PASSWORD = PropertiesProvider.getRequiredProperty("db.password");

    private JdbcProvider(){
    }

    private static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (JdbcProvider.class) {
                if (dataSource == null) {
                    dataSource = createDataSource(HOST, PORT, DB_NAME, DB_USER, DB_PASSWORD);
                }
            }
        }
        return dataSource;
    }

    private static DataSource createDataSource(String host,
                                              String port,
                                              String dbName,
                                              String dbUser,
                                              String dbPassword) {

        try {
            PGSimpleDataSource dataSource = new PGSimpleDataSource();
            dataSource.setServerName(host);
            dataSource.setPortNumber(Integer.valueOf(port));
            dataSource.setDatabaseName(dbName);
            dataSource.setUser(dbUser);
            dataSource.setPassword(dbPassword);
            return dataSource;
        } catch (Exception e) {
            throw new RuntimeException("Unable to create JDBC DataSource", e);
        }
    }

    public static JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(getDataSource());
        }
        return jdbcTemplate;
    }
}
