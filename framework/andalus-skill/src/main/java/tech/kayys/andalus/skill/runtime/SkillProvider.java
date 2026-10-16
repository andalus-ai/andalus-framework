package tech.kayys.andalus.skill.runtime;

import tech.kayys.andalus.skill.spi.Skill;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Source of {@link Skill} implementations for a particular backend.
 *
 * <p>Examples of concrete providers:</p>
 * <ul>
 *   <li>{@code BuiltinSkillProvider} — framework built-in skills</li>
 *   <li>{@code PluginSkillProvider} — skills loaded from installed plugins</li>
 *   <li>{@code WorkspaceSkillProvider} — skills found in the active workspace</li>
 *   <li>{@code RemoteSkillProvider} — skills obtained from a remote registry</li>
 * </ul>
 *
 * <p>A provider must <strong>not</strong> call the {@link SkillResolver} that
 * delegates to it; provider → resolver dependency would create an infinite loop.</p>
 */
public interface SkillProvider {

    /** Stable identifier for this provider (e.g., {@code "builtin"}, {@code "plugin"}). */
    String id();

    /**
     * Attempts to supply the skill described by {@code request}.
     *
     * @param request the resolution request
     * @return a stage completing with the skill, or {@link Optional#empty()} when
     *         this provider cannot satisfy the request
     */
    CompletionStage<Optional<Skill>> resolve(
            SkillResolutionRequest request
    );
}
