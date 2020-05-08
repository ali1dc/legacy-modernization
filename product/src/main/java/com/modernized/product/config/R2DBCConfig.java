package com.modernized.product.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.modernized.product.repository")
public class R2DBCConfig extends AbstractR2dbcConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value(value = "${spring.r2dbc.host}")
    private String host;
    @Value(value = "${spring.r2dbc.port}")
    private String port;
    @Value(value = "${spring.r2dbc.username}")
    private String username;
    @Value(value = "${spring.r2dbc.password}")
    private String password;
    @Value(value = "${spring.r2dbc.database}")
    private String database;

    @Bean
    public ConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(host)
                        .port(Integer.parseInt(port))
                        .username(username)
                        .password(password)
                        .database(database)
                        .build());
    }
}