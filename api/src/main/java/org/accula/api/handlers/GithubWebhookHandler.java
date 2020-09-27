package org.accula.api.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accula.api.db.model.Pull;
import org.accula.api.db.repo.ProjectRepo;
import org.accula.api.github.model.GithubApiHookPayload;
import org.accula.api.handlers.util.ProjectUpdater;
import org.accula.api.service.CloneDetectionService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author Anton Lamtev
 * @author Vadim Dyachkov
 */
@Component
@Slf4j
@RequiredArgsConstructor
public final class GithubWebhookHandler {
    private static final String GITHUB_EVENT = "X-GitHub-Event";
    private static final String GITHUB_EVENT_PING = "ping";
    private static final String GITHUB_EVENT_PULL = "pull_request";

    private final ProjectRepo projectRepo;
    private final ProjectUpdater projectUpdater;
    private final CloneDetectionService cloneDetectionService;

    public Mono<ServerResponse> webhook(final ServerRequest request) {
        // TODO: validate signature in X-Hub-Signature
        return switch (Objects.requireNonNull(request.headers().firstHeader(GITHUB_EVENT))) {
            case GITHUB_EVENT_PING -> ok();
            case GITHUB_EVENT_PULL -> processPull(request);
            default -> ServerResponse.badRequest().build();
        };
    }

    private static Mono<ServerResponse> ok() {
        return ServerResponse.ok().build();
    }

    private Mono<ServerResponse> processPull(final ServerRequest request) {
        return request
                .bodyToMono(GithubApiHookPayload.class)
                .onErrorResume(GithubWebhookHandler::ignoreNotSupportedAction)
                .flatMap(this::processPayload)
                .onErrorResume(e -> {
                    log.error("Error during payload processing: ", e);
                    return Mono.empty();
                })
                .flatMap(__ -> ok());
    }

    private Mono<Void> processPayload(final GithubApiHookPayload payload) {
        return switch (payload.getAction()) {
            case OPENED, SYNCHRONIZE -> updateProject(payload).transform(this::detectClones);
            case EDITED, CLOSED -> updateProject(payload).then();
        };
    }

    private Mono<Pull> updateProject(final GithubApiHookPayload payload) {
        final var githubApiPull = payload.getPull();

        return projectRepo
                .idByRepoId(payload.getRepo().getId())
                .flatMap(projectId -> projectUpdater.update(projectId, githubApiPull));
    }

    private Mono<Void> detectClones(final Mono<Pull> pull) {
        return pull
                .flatMapMany(cloneDetectionService::detectClones)
                .then();
    }

    private static <E extends Throwable, T> Mono<T> ignoreNotSupportedAction(final E error) {
        return Mono.empty();
    }
}
