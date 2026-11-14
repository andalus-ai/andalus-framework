package tech.kayys.andalus.inference.bridge.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import tech.kayys.andalus.harness.inference.DefaultInferenceRuntime;
import tech.kayys.andalus.harness.inference.InferenceProviderAdapter;
import tech.kayys.andalus.harness.inference.InferenceRuntime;
import tech.kayys.andalus.inference.InferenceProvider;
import tech.kayys.andalus.inference.ModelInfo;
import tech.kayys.andalus.inference.bridge.AndalusInferenceProviderAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Quarkus composition root that exposes one inference runtime backed by all
 * CDI-discovered legacy Andalus inference providers.
 *
 * <p>The provider identifier is resolved from each advertised model's
 * {@link ModelInfo#provider()} field. Missing or inconsistent identity is a
 * configuration error; this producer never guesses a vendor identifier.</p>
 */
@ApplicationScoped
public class AndalusInferenceRuntimeProducer {

    @Produces
    @ApplicationScoped
    public InferenceRuntime inferenceRuntime(Instance<InferenceProvider> providers) {
        return createRuntime(providers);
    }

    static InferenceRuntime createRuntime(Iterable<InferenceProvider> providers) {
        List<InferenceProviderAdapter> adapters = new ArrayList<>();
        Set<String> providerIds = new HashSet<>();

        for (InferenceProvider provider : providers) {
            if (provider == null) {
                continue;
            }
            Set<String> models = provider.listModels();
            if (models == null || models.isEmpty()) {
                continue;
            }

            String providerId = resolveProviderId(provider, models);
            if (!providerIds.add(providerId)) {
                throw new IllegalStateException(
                        "Multiple CDI inference providers advertise provider ID '" + providerId + "'");
            }
            adapters.add(new AndalusInferenceProviderAdapter(providerId, provider));
        }

        return new DefaultInferenceRuntime(adapters);
    }

    private static String resolveProviderId(InferenceProvider provider, Set<String> models) {
        String resolved = null;
        for (String model : models) {
            if (model == null || model.isBlank()) {
                continue;
            }
            ModelInfo info = provider.getModelInfo(model);
            String candidate = info == null ? null : info.provider();
            if (candidate == null || candidate.isBlank()) {
                throw new IllegalStateException(
                        "Provider '" + provider.getClass().getName()
                                + "' does not report a provider ID for model '" + model + "'");
            }
            if (resolved == null) {
                resolved = candidate.trim();
            } else if (!resolved.equals(candidate.trim())) {
                throw new IllegalStateException(
                        "Provider '" + provider.getClass().getName()
                                + "' reports inconsistent provider IDs across its models");
            }
        }
        if (resolved == null) {
            throw new IllegalStateException(
                    "Provider '" + provider.getClass().getName() + "' advertises no valid model identifiers");
        }
        return resolved;
    }
}
