package tech.kayys.andalus.skill.observability;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable context capturing identity and timing for observing a single
 * Skill execution invocation.
 */
public record SkillObservationContext(
        String invocationId,
        String executionId,
        String traceId,
        String turnId,
        String skillId,
        String skillVersion,
        Instant startedAt
) {

    public SkillObservationContext {
        Objects.requireNonNull(invocationId, "invocationId must not be null");
        executionId  = executionId  == null ? "" : executionId;
        traceId      = traceId      == null ? "" : traceId;
        turnId       = turnId       == null ? "" : turnId;
        skillId      = skillId      == null ? "" : skillId;
        skillVersion = skillVersion == null ? "" : skillVersion;
        startedAt    = startedAt    == null ? Instant.now() : startedAt;
    }
}
