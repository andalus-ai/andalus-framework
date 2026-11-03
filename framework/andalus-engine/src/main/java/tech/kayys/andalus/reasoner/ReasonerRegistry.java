package tech.kayys.andalus.reasoner;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Resolves a reasoner for a declared reasoning strategy.
 *
 * <p>The registry deliberately does not choose an arbitrary provider when
 * multiple reasoners claim the same strategy. Ambiguous registrations fail
 * during construction so an application cannot silently execute a different
 * reasoning implementation after a classpath or deployment change.</p>
 *
 * <p>Runtime modules are responsible for discovering and supplying providers
 * (for example through CDI or Java service loading). This class stays free of
 * container-specific dependencies.</p>
 */
public final class ReasonerRegistry {

    private final Map<ReasoningStrategy, Reasoner> reasoners;

    public ReasonerRegistry(Collection<? extends Reasoner> providers) {
        Objects.requireNonNull(providers, "providers must not be null");

        EnumMap<ReasoningStrategy, Reasoner> registrations =
                new EnumMap<>(ReasoningStrategy.class);

        for (Reasoner provider : providers) {
            Objects.requireNonNull(provider, "reasoner provider must not be null");
            Set<ReasoningStrategy> strategies = Objects.requireNonNull(
                    provider.getSupportedStrategies(),
                    "reasoner supported strategies must not be null");

            if (strategies.isEmpty()) {
                throw new IllegalArgumentException(
                        "Reasoner " + provider.getClass().getName()
                                + " does not declare any supported strategies");
            }

            for (ReasoningStrategy strategy : strategies) {
                Objects.requireNonNull(strategy,
                        "reasoner strategy must not be null");
                Reasoner previous = registrations.putIfAbsent(strategy, provider);
                if (previous != null && previous != provider) {
                    throw new IllegalArgumentException(
                            "Multiple reasoners registered for strategy " + strategy
                                    + ": " + previous.getClass().getName()
                                    + " and " + provider.getClass().getName());
                }
            }
        }

        this.reasoners = Collections.unmodifiableMap(registrations);
    }

    /** Returns the provider for a strategy, if one is registered. */
    public Optional<Reasoner> find(ReasoningStrategy strategy) {
        Objects.requireNonNull(strategy, "strategy must not be null");
        return Optional.ofNullable(reasoners.get(strategy));
    }

    /**
     * Returns the provider for a strategy or fails with a descriptive error.
     */
    public Reasoner require(ReasoningStrategy strategy) {
        Objects.requireNonNull(strategy, "strategy must not be null");
        Reasoner reasoner = reasoners.get(strategy);
        if (reasoner == null) {
            throw new IllegalArgumentException(
                    "No reasoner is registered for strategy " + strategy
                            + ". Registered strategies: " + reasoners.keySet());
        }
        return reasoner;
    }

    /** Returns an immutable snapshot of the registered strategies. */
    public Set<ReasoningStrategy> supportedStrategies() {
        return reasoners.keySet();
    }
}
