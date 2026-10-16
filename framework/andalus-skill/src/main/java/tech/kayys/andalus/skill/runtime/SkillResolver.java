package tech.kayys.andalus.skill.runtime;

import tech.kayys.andalus.skill.spi.Skill;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Resolves a {@link SkillResolutionRequest} to an executable {@link Skill}.
 *
 * <p>Resolution is separate from execution.  The resolver determines
 * <em>which</em> registered implementation satisfies a reference; the
 * {@link SkillRuntime} then executes it.  Not finding a skill is a normal
 * result ({@link Optional#empty()}) — the caller decides whether this is a
 * configuration error, marketplace opportunity, or execution failure.</p>
 *
 * <p>The resolver must never install, execute, authorize, or mutate the
 * registry.  Those operations belong to separate lifecycle concerns.</p>
 */
public interface SkillResolver {

    /**
     * Resolves the given request to a {@link Skill}.
     *
     * @param request the resolution request; must not be {@code null}
     * @return a stage that completes with the resolved skill, or
     *         {@link Optional#empty()} when no provider can satisfy the request
     */
    CompletionStage<Optional<Skill>> resolve(
            SkillResolutionRequest request
    );
}
