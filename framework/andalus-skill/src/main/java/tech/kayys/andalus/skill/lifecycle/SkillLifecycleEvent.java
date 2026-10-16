package tech.kayys.andalus.skill.lifecycle;

import tech.kayys.andalus.skill.spi.SkillReference;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable record of a lifecycle transition for a Skill.
 * Emitted by {@link SkillLifecycleManager} implementations for each state change.
 */
public record SkillLifecycleEvent(
        SkillReference skill,
        SkillLifecycleState previousState,
        SkillLifecycleState newState,
        Instant occurredAt,
        String reason
) {

    public SkillLifecycleEvent {
        Objects.requireNonNull(skill, "skill");
        Objects.requireNonNull(newState, "newState");
        occurredAt = occurredAt == null ? Instant.now() : occurredAt;
        reason     = reason     == null ? ""            : reason;
    }

    /** Convenience factory using {@code Instant.now()} and no reason. */
    public static SkillLifecycleEvent of(
            SkillReference skill,
            SkillLifecycleState from,
            SkillLifecycleState to) {

        return new SkillLifecycleEvent(skill, from, to, Instant.now(), "");
    }

    /** Convenience factory with an explicit reason string. */
    public static SkillLifecycleEvent of(
            SkillReference skill,
            SkillLifecycleState from,
            SkillLifecycleState to,
            String reason) {

        return new SkillLifecycleEvent(skill, from, to, Instant.now(), reason);
    }
}
