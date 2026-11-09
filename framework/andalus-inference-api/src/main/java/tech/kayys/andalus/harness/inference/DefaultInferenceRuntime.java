package tech.kayys.andalus.harness.inference;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of {@link InferenceRuntime}.
 *
 * <p>Resolves the appropriate {@link InferenceProviderAdapter} based on the plan's requested
 * provider and model ID, enforces latency boundaries, and standardizes failure results.</p>
 */
public class DefaultInferenceRuntime implements InferenceRuntime {

    private static final Logger LOG = Logger.getLogger(DefaultInferenceRuntime.class.getName());

    private final ConcurrentHashMap<String, InferenceProviderAdapter> providers = new ConcurrentHashMap<>();

    public DefaultInferenceRuntime() {}

    public DefaultInferenceRuntime(Collection<InferenceProviderAdapter> initialProviders) {
        if (initialProviders != null) {
            for (InferenceProviderAdapter p : initialProviders) {
                registerProvider(p);
            }
        }
    }

    public void registerProvider(InferenceProviderAdapter provider) {
        if (provider != null) {
            InferenceProviderAdapter existing = providers.putIfAbsent(provider.providerId(), provider);
            if (existing != null && existing != provider) {
                throw new IllegalArgumentException(
                        "An inference provider is already registered with ID: " + provider.providerId());
            }
        }
    }

    public Optional<InferenceProviderAdapter> findProvider(String providerId) {
        return Optional.ofNullable(providers.get(providerId));
    }

    @Override
    public CompletionStage<InferenceResult> execute(InferenceRequest request) {
        if (request == null || request.plan() == null || request.input() == null) {
            return CompletableFuture.completedFuture(
                    InferenceResult.failed(InferenceStatus.REQUEST_INVALID, "Invalid inference request")
            );
        }

        InferencePlan plan = request.plan();
        Optional<InferenceProviderAdapter> providerOpt = resolveProvider(plan);

        if (providerOpt.isEmpty()) {
            return CompletableFuture.completedFuture(
                    InferenceResult.failed(
                            InferenceStatus.PROVIDER_UNAVAILABLE,
                            providerResolutionFailure(plan)
                    )
            );
        }

        InferenceProviderAdapter provider = providerOpt.get();
        long maxLatency = plan.constraints().maxLatencyMillis();

        CompletableFuture<InferenceResult> executionFuture = provider.execute(request)
                .toCompletableFuture();

        if (maxLatency > 0) {
            executionFuture = executionFuture.orTimeout(maxLatency, TimeUnit.MILLISECONDS)
                    .exceptionally(ex -> {
                        if (ex instanceof java.util.concurrent.TimeoutException) {
                            LOG.log(Level.WARNING, "Inference operation timed out after " + maxLatency + "ms");
                            return InferenceResult.failed(InferenceStatus.TIMEOUT, "Inference timed out after " + maxLatency + "ms");
                        }
                        LOG.log(Level.WARNING, "Inference execution error", ex);
                        return InferenceResult.failed(InferenceStatus.FAILED, ex.getMessage());
                    });
        } else {
            executionFuture = executionFuture.exceptionally(ex -> {
                LOG.log(Level.WARNING, "Inference execution error", ex);
                return InferenceResult.failed(InferenceStatus.FAILED, ex.getMessage());
            });
        }

        return executionFuture;
    }

    private Optional<InferenceProviderAdapter> resolveProvider(InferencePlan plan) {
        if (plan.providerId().isPresent()) {
            InferenceProviderAdapter selected = providers.get(plan.providerId().get());
            return selected != null && selected.supports(plan.modelId())
                    ? Optional.of(selected)
                    : Optional.empty();
        }

        // Never make provider choice depend on ConcurrentHashMap iteration order.
        // An explicit provider ID is required when multiple adapters support the model.
        List<InferenceProviderAdapter> compatible = providers.values().stream()
                .filter(provider -> provider.supports(plan.modelId()))
                .toList();
        return compatible.size() == 1 ? Optional.of(compatible.getFirst()) : Optional.empty();
    }

    private String providerResolutionFailure(InferencePlan plan) {
        if (plan.providerId().isPresent()) {
            return "Provider '" + plan.providerId().get()
                    + "' is unavailable or does not support model: " + plan.modelId();
        }
        long compatibleCount = providers.values().stream()
                .filter(provider -> provider.supports(plan.modelId()))
                .count();
        return compatibleCount > 1
                ? "Multiple providers support model '" + plan.modelId()
                        + "'; specify an explicit provider ID"
                : "No provider available for model: " + plan.modelId();
    }
}
