package tech.kayys.andalus.skill.runtime;

import tech.kayys.andalus.skill.spi.SkillResult;

import java.util.concurrent.CompletionStage;

/**
 * Asynchronous execution boundary for {@link tech.kayys.andalus.skill.spi.Skill}.
 *
 * <p>The existing {@link tech.kayys.andalus.skill.spi.Skill#execute} method is
 * synchronous.  {@code SkillRuntime} wraps it in an async boundary so that a
 * blocking skill implementation does not force the entire agent runtime to
 * become synchronous.</p>
 *
 * <p>The runtime is responsible for:</p>
 * <ul>
 *   <li>resolution (via {@link SkillResolver})</li>
 *   <li>context construction</li>
 *   <li>timeout enforcement (local guard; authoritative timeout lives in the
 *       execution coordinator)</li>
 *   <li>result normalization (null → failure)</li>
 *   <li>exception normalization</li>
 * </ul>
 *
 * <p>Retries, checkpointing, cancellation, journaling, and authorization are
 * <strong>not</strong> responsibilities of the runtime; those belong to the
 * execution coordinator, policy subsystem, and observability layers.</p>
 */
public interface SkillRuntime {

    /**
     * Executes the skill described by {@code request}.
     *
     * @param request the execution request; must not be {@code null}
     * @return a stage that completes with the skill result, always non-null
     *         (failures are expressed as {@link SkillResult#failure})
     */
    CompletionStage<SkillResult> execute(
            SkillExecutionRequest request
    );
}
