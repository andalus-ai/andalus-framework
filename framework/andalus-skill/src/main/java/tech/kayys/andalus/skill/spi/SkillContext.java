package tech.kayys.andalus.skill.spi;

import tech.kayys.andalus.identity.ResourceId;

import java.util.Map;
import java.util.Optional;

/**
 * Runtime context supplied to a {@link Skill} invocation.
 *
 * <p>The context carries immutable invocation identity, inputs, and runtime
 * constraints. Runtime services (models, databases, HTTP clients, memory
 * stores) are intentionally <strong>not</strong> exposed here — that would turn
 * SkillContext into a service locator. Skills access governed infrastructure
 * through declared capability dependencies resolved by {@code SkillRuntime}.</p>
 *
 * <p>All methods have backward-compatible defaults so existing Skill
 * implementations that only use {@link #getInputs()} continue to compile and
 * run unchanged.</p>
 */
public interface SkillContext {

    /**
     * Inputs supplied to this skill invocation. Never {@code null}.
     */
    Map<String, Object> getInputs();

    /**
     * Unique identifier of this skill invocation (generated per call).
     */
    default String getInvocationId() {
        return "";
    }

    /**
     * Identifier of the enclosing Andalus agent execution.
     */
    default String getExecutionId() {
        return "";
    }

    /**
     * Distributed trace identifier for cross-service correlation.
     */
    default String getTraceId() {
        return "";
    }

    /**
     * Optional parent agent-turn identifier.
     */
    default String getTurnId() {
        return "";
    }

    /**
     * Execution-scoped metadata map. Never {@code null}.
     */
    default Map<String, Object> getAttributes() {
        return Map.of();
    }

    // ── Convenience aliases (non-colliding, Optional-returning) ──────────────

    /**
     * Agent identity, if propagated by the caller.
     */
    default Optional<ResourceId> agentId() {
        return Optional.empty();
    }

    /**
     * Returns {@link #getExecutionId()} wrapped in an {@code Optional},
     * present only when the value is non-blank.
     */
    default Optional<String> executionIdOptional() {
        String execId = getExecutionId();
        return execId != null && !execId.isBlank()
                ? Optional.of(execId)
                : Optional.empty();
    }

    /**
     * Returns {@link #getTurnId()} wrapped in an {@code Optional},
     * present only when the value is non-blank.
     */
    default Optional<String> turnIdOptional() {
        String tid = getTurnId();
        return tid != null && !tid.isBlank()
                ? Optional.of(tid)
                : Optional.empty();
    }

    /**
     * Service-locator hook — intentionally returns empty by default.
     * Prefer capability-declared dependencies over this mechanism.
     *
     * @deprecated Use the capability system; this method exists for
     *             transitional compatibility only.
     */
    @Deprecated
    default <T> Optional<T> service(Class<T> type) {
        return Optional.empty();
    }

    /**
     * Alias for {@link #getAttributes()}. Provided for fluent call-site style.
     */
    default Map<String, Object> attributes() {
        return getAttributes();
    }
}
