package org.accula.api.handlers;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class AdminHandler {
    private static final String MESSAGE = "You are an admin now.";

    @NotNull
    public Mono<ServerResponse> getAdmin() {
        return ok().bodyValue(MESSAGE);
    }
}