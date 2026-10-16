package tech.kayys.andalus.skill.lifecycle;

import java.util.concurrent.CompletionStage;

/**
 * Optional lifecycle SPI that a Skill implementation may implement to
 * participate in explicit activation and deactivation.
 *
 * <p>Existing Skill implementations are <strong>not</strong> required to
 * implement this interface. The {@link SkillLifecycleManager} governs
 * lifecycle transitions regardless of whether the Skill itself participates.</p>
 */
public interface SkillLifecycle {

    /** The current lifecycle state of this Skill. */
    SkillLifecycleState state();

    /**
     * Called when the runtime transitions this Skill to {@link SkillLifecycleState#ACTIVE}.
     * Implementations may acquire resources, warm caches, start background tasks, etc.
     *
     * @return a stage that completes when activation is finished
     */
    CompletionStage<Void> activate();

    /**
     * Called when the runtime transitions this Skill to {@link SkillLifecycleState#DISABLED}.
     * Implementations should release held resources gracefully.
     * In-flight executions may continue to completion.
     *
     * @return a stage that completes when deactivation is finished
     */
    CompletionStage<Void> deactivate();
}
