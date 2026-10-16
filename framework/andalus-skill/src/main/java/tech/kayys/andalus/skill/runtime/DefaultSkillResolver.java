package tech.kayys.andalus.skill.runtime;

import tech.kayys.andalus.skill.spi.Skill;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Chains a list of {@link SkillProvider}s in order, returning the first
 * successful resolution.
 *
 * <p>The resolver deliberately does <strong>not</strong>:</p>
 * <ul>
 *   <li>install or activate skills</li>
 *   <li>execute skill logic</li>
 *   <li>authorize callers</li>
 *   <li>mutate the registry</li>
 * </ul>
 *
 * <p>It only resolves.  Installation is a separate management lifecycle and
 * authorization belongs to the policy/governance subsystem.</p>
 */
public final class DefaultSkillResolver
        implements SkillResolver {

    private final List<SkillProvider> providers;

    public DefaultSkillResolver(
            List<SkillProvider> providers) {

        this.providers =
                List.copyOf(
                        Objects.requireNonNull(
                                providers,
                                "providers must not be null"
                        )
                );
    }

    @Override
    public CompletionStage<Optional<Skill>> resolve(
            SkillResolutionRequest request) {

        Objects.requireNonNull(
                request,
                "request must not be null"
        );

        return resolveFrom(request, 0);
    }

    // --- internal ---

    private CompletionStage<Optional<Skill>> resolveFrom(
            SkillResolutionRequest request,
            int index) {

        if (index >= providers.size()) {
            return CompletableFuture.completedFuture(
                    Optional.empty()
            );
        }

        SkillProvider provider = providers.get(index);

        return provider
                .resolve(request)
                .thenCompose(result -> {

                    if (result.isPresent()) {
                        return CompletableFuture.completedFuture(result);
                    }

                    return resolveFrom(request, index + 1);
                });
    }
}
