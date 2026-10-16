package tech.kayys.andalus.skill.runtime;

import tech.kayys.andalus.skill.spi.SkillReference;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Self-contained description of a single skill execution request.
 *
 * <p>Carries both the execution target ({@link SkillReference}) and the
 * <em>execution lineage</em> (executionId / traceId / turnId) so that
 * observability tools can correlate Skill invocations with the enclosing
 * agent execution without requiring out-of-band propagation.</p>
 *
 * <p>Using a record keeps the {@link SkillRuntime#execute} signature stable as
 * new metadata needs arise — the contract never needs to change.</p>
 */
public record SkillExecutionRequest(
        SkillReference skill,
        Map<String, Object> inputs,
        Optional<Duration> timeout,
        String executionId,
        String traceId,
        String turnId,
        Map<String, Object> attributes
) {

    public SkillExecutionRequest {
        Objects.requireNonNull(skill, "skill must not be null");

        inputs = inputs == null
                ? Map.of()
                : Map.copyOf(inputs);

        timeout = timeout == null
                ? Optional.empty()
                : timeout;

        executionId = executionId == null ? "" : executionId;
        traceId     = traceId     == null ? "" : traceId;
        turnId      = turnId      == null ? "" : turnId;

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    /**
     * Backward-compatible 4-arg constructor without explicit lineage.
     */
    public SkillExecutionRequest(
            SkillReference skill,
            Map<String, Object> inputs,
            Optional<Duration> timeout,
            Map<String, Object> attributes) {
        this(skill, inputs, timeout, "", "", "", attributes);
    }

    /**
     * Convenience factory for requests without a timeout, lineage, or extra attributes.
     */
    public static SkillExecutionRequest of(
            SkillReference skill,
            Map<String, Object> inputs) {

        return new SkillExecutionRequest(
                skill, inputs, Optional.empty(), "", "", "", Map.of());
    }

    /**
     * Convenience factory supplying execution lineage.
     */
    public static SkillExecutionRequest of(
            SkillReference skill,
            Map<String, Object> inputs,
            String executionId,
            String traceId,
            String turnId) {

        return new SkillExecutionRequest(
                skill, inputs, Optional.empty(), executionId, traceId, turnId, Map.of());
    }
}
