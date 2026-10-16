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
            providers.put(provider.providerId(), provider);
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
                            "No provider available for model: " + plan.modelId()
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
            InferenceProviderAdapter byId = providers.get(plan.providerId().get());
            if (byId != null) {
                return Optional.of(byId);
            }
        }

        // Fall back to first provider supporting the model
        return providers.values().stream()
                .filter(p -> p.supports(plan.modelId()))
                .findFirst();
    }
}
