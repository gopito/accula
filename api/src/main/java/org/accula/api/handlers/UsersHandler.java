package org.accula.api.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accula.api.converter.ModelToDtoConverter;
import org.accula.api.db.repo.UserRepo;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Handles all {@code /users} routes
 *
 * @author Anton Lamtev
 */
@Slf4j
@Component
@RequiredArgsConstructor
public final class UsersHandler {
    private static final Exception USER_NOT_FOUND_EXCEPTION = new Exception();

    private final UserRepo userRepo;
    private final ModelToDtoConverter modelToDtoConverter;

    public Mono<ServerResponse> getById(final ServerRequest request) {
        return Mono
                .justOrEmpty(request.pathVariable("id"))
                .map(Long::parseLong)
                .onErrorMap(NumberFormatException.class, e -> USER_NOT_FOUND_EXCEPTION)
                .flatMap(userRepo::findById)
                .map(modelToDtoConverter::convert)
                .flatMap(user -> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(Mono.error(USER_NOT_FOUND_EXCEPTION))
                .onErrorResume(USER_NOT_FOUND_EXCEPTION::equals, e -> ServerResponse.notFound().build());
    }
}
