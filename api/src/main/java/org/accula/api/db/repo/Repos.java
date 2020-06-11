package org.accula.api.db.repo;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Anton Lamtev
 */
final class Repos {
    private Repos() {
    }

    static <T> Mono<T> closeAndReturn(final Connection connection, final T value) {
        return Mono.from(connection.close()).thenReturn(value);
    }

    static <T> Flux<T> closeAndReturn(final Connection connection, final Flux<T> values) {
        return Mono.from(connection.close()).thenMany(values);
    }

    static <T> Flux<T> closeAndReturn(final Connection connection, final Iterable<T> values) {
        return Mono.from(connection.close()).thenMany(Flux.fromIterable(values));
    }

    static <T> Mono<T> column(final Result result, final String name, final Class<T> clazz) {
        return Mono.from(result
                .map((row, metadata) -> Objects.requireNonNull(row.get(name, clazz))));
    }

    static <T> Mono<T> closeAndGet(final Connection connection, final Result result, final String name, final Class<T> clazz) {
        return column(result, name, clazz)
                .flatMap(column -> closeAndReturn(connection, column));
    }

    static <T> Mono<T> convert(final Result result, final Connection connection, final Function<Row, T> transform) {
        return Mono.from(result.map((row, metadata) -> transform.apply(row)))
                .flatMap(res -> closeAndReturn(connection, res));
    }

    static <T> Flux<T> convertMany(final Result result, final Connection connection, final Function<Row, T> transform) {
        final var results = Flux.from(result.map((row, metadata) -> transform.apply(row))).cache();
        return results.thenMany(closeAndReturn(connection, results));
    }

    static <T> Flux<T> convertMany(final Flux<? extends Result> result, final Connection connection, final Function<Row, T> transform) {
        final var results = result
                .flatMap(res -> res
                        .map((row, metadata) -> transform.apply(row)))
                .cache();
        return results.thenMany(closeAndReturn(connection, results));
    }
}
