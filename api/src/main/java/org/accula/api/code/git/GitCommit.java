package org.accula.api.code.git;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Builder(builderClassName = "Builder")
@Value
public class GitCommit {
    String sha;
    String authorName;
    String authorEmail;
    Instant date;
}
