package org.accula.api.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.RequiredArgsConstructor;
import org.accula.api.db.repo.ConnectionProvidedRepo;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import static io.r2dbc.pool.PoolingConnectionFactoryProvider.INITIAL_SIZE;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

/**
 * @author Anton Lamtev
 */
@Configuration
@EnableR2dbcRepositories
@EnableConfigurationProperties(DbProperties.class)
@RequiredArgsConstructor
public class DbConfig extends AbstractR2dbcConfiguration {
    private final DbProperties dbProperties;

    @Bean
    @Override
    public ConnectionPool connectionFactory() {
        final var pool = dbProperties.getPool();

        final var connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "postgresql")
                .option(HOST, dbProperties.getHost())
                .option(PORT, dbProperties.getPort())
                .option(USER, dbProperties.getUser())
                .option(PASSWORD, dbProperties.getPassword())
                .option(DATABASE, dbProperties.getDatabase())
                .option(INITIAL_SIZE, pool.getMinSize())
                .option(MAX_SIZE, pool.getMaxSize())
                .build());

        final var poolConfig = ConnectionPoolConfiguration.builder(connectionFactory)
                .maxIdleTime(pool.getMaxIdleTime())
                .build();

        return new ConnectionPool(poolConfig);
    }

    @Bean
    public ConnectionProvidedRepo.ConnectionProvider connectionProvider(final ConnectionPool connectionPool) {
        return connectionPool::create;
    }
}
