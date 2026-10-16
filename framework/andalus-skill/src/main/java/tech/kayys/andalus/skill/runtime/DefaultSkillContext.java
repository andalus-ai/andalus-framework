package tech.kayys.andalus.skill.runtime;

import tech.kayys.andalus.skill.spi.SkillContext;
import tech.kayys.andalus.skill.spi.SkillRuntimeConstraints;

import java.util.Map;
import java.util.Objects;

/**
 * Concrete {@link SkillContext} constructed by the skill runtime for each
 * invocation.
 *
 * <p>Inputs and attributes are defensively copied to guarantee immutability.
 * A skill implementation must not be able to mutate the context it receives.</p>
 *
 * <p>Services such as model clients, databases, or filesystem access are
 * <strong>not</strong> exposed here.  Skills access those through their own
 * injected dependencies or through the capability/tool SPI, keeping the context
 * as a lightweight data carrier rather than a service locator.</p>
 */
public record DefaultSkillContext(
        Map<String, Object> inputs,
        String invocationId,
        String executionId,
        String traceId,
        String turnId,
        Map<String, Object> attributes,
        SkillRuntimeConstraints constraints
) implements SkillContext {
    public DefaultSkillContext {
        inputs = inputs == null ? Map.of() : Map.copyOf(inputs);
        invocationId = Objects.requireNonNull(invocationId, "invocationId must not be null");
        executionId = executionId == null ? "" : executionId;
        traceId = traceId == null ? "" : traceId;
        turnId = turnId == null ? "" : turnId;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        constraints = constraints == null ? SkillRuntimeConstraints.unlimited() : constraints;
    }

    /**
     * Backward-compatible 5-arg constructor without turnId and custom constraints.
     */
    public DefaultSkillContext(
            Map<String, Object> inputs,
            String invocationId,
            String executionId,
            String traceId,
            Map<String, Object> attributes) {
        this(inputs, invocationId, executionId, traceId, "", attributes, SkillRuntimeConstraints.unlimited());
    }

    @Override public Map<String, Object> getInputs() { return inputs; }
    @Override public String getInvocationId() { return invocationId; }
    @Override public String getExecutionId() { return executionId; }
    @Override public String getTraceId() { return traceId; }
    @Override public String getTurnId() { return turnId; }
    @Override public Map<String, Object> getAttributes() { return attributes; }
}
