package tech.kayys.andalus.skill.runtime;

import tech.kayys.andalus.skill.spi.Skill;
import tech.kayys.andalus.skill.spi.SkillRegistry;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Adapts the existing {@link SkillRegistry} into the {@link SkillProvider} SPI.
 *
 * <p>This is the default first-party provider.  By wrapping the registry
 * rather than replacing it, all existing registration code remains intact
 * and gains resolver integration at no cost.</p>
 *
 * <p>Version-aware selection is intentionally not implemented here yet;
 * the registry currently stores one instance per ID.  A versioned registry
 * can be added later without changing the {@link SkillResolver} contract.</p>
 */
public final class RegistrySkillResolver
        implements SkillProvider {

    private static final String PROVIDER_ID = "registry";

    private final SkillRegistry registry;

    public RegistrySkillResolver(SkillRegistry registry) {
        this.registry =
                Objects.requireNonNull(
                        registry,
                        "registry must not be null"
                );
    }

    @Override
    public String id() {
        return PROVIDER_ID;
    }

    @Override
    public CompletionStage<Optional<Skill>> resolve(
            SkillResolutionRequest request) {

        Optional<Skill> result =
                registry.findById(
                        request.reference().id()
                );

        return CompletableFuture.completedFuture(result);
    }
}
