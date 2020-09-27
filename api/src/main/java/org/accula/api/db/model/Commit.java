package org.accula.api.db.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * @author Anton Lamtev
 */
@Builder
@Value
public class Commit {
    String sha;
    String authorName;
    String authorEmail;
    Instant date;
}
