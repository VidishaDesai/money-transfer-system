package com.example.money_transfer_system.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
@Configuration
public class SnowflakeConfig {

    @Bean
    public DataSource snowflakeDataSource(
            @Value("${snowflake.datasource.url}") String url,
            @Value("${snowflake.datasource.username}") String username,
            @Value("${snowflake.datasource.password}") String password
    ) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("net.snowflake.client.jdbc.SnowflakeDriver");
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    @Bean
    public JdbcTemplate snowflakeJdbcTemplate(
            @Qualifier("snowflakeDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
