package tech.kayys.andalus.skill.lifecycle;

import tech.kayys.andalus.skill.spi.SkillReference;

import java.util.concurrent.CompletionStage;

/**
 * Governs the installation lifecycle of Skills.
 *
 * <p>Responsibilities are strictly limited to lifecycle transitions — this
 * interface does <strong>not</strong> execute Skills. Execution is the
 * responsibility of {@code SkillRuntime}.</p>
 *
 * <pre>
 * SkillLifecycleManager          SkillRuntime
 *   ├── activate()         vs      ├── execute()
 *   ├── deactivate()               └── resolve()
 *   └── uninstall()
 * </pre>
 *
 * <p>Version coexistence is supported: multiple versions of the same Skill
 * may be installed, with only one (or none) active at a time per version
 * selection policy.</p>
 */
public interface SkillLifecycleManager {

    /**
     * Transition the Skill to {@link SkillLifecycleState#ACTIVE}.
     *
     * @param reference the Skill to activate
     * @return a stage that completes with the resulting state
     */
    CompletionStage<SkillLifecycleState> activate(SkillReference reference);

    /**
     * Transition the Skill to {@link SkillLifecycleState#DISABLED}.
     * In-flight executions may continue to completion.
     *
     * @param reference the Skill to deactivate
     * @return a stage that completes with the resulting state
     */
    CompletionStage<SkillLifecycleState> deactivate(SkillReference reference);

    /**
     * Remove the Skill from the registry and storage.
     * Active Skills must be deactivated before uninstalling.
     *
     * @param reference the Skill to uninstall
     * @return a stage that completes when uninstall is done
     */
    CompletionStage<Void> uninstall(SkillReference reference);

    /**
     * Return the current lifecycle state for the given reference.
     *
     * @param reference the Skill to query; never {@code null}
     * @return current state, or {@link SkillLifecycleState#DISCOVERED} if unknown
     */
    SkillLifecycleState currentState(SkillReference reference);
}
