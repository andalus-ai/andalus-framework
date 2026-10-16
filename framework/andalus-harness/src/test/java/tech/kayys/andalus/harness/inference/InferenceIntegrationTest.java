package tech.kayys.andalus.harness.inference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.assertj.core.api.Assertions.assertThat;

class InferenceIntegrationTest {

    private DefaultInferenceRuntime runtime;

    @BeforeEach
    void setUp() {
        runtime = new DefaultInferenceRuntime();
    }

    @Test
    void successfulExecution_returnsCompletedResult() {
        InferenceProviderAdapter mockProvider = new InferenceProviderAdapter() {
            @Override public String providerId() { return "mock-aqli"; }
            @Override public boolean supports(String modelId) { return "llama-3-8b".equals(modelId); }
            @Override public CompletionStage<InferenceResult> execute(InferenceRequest req) {
                InferenceUsage usage = InferenceUsage.of(120, 45, 150);
                return CompletableFuture.completedFuture(
                        InferenceResult.completed("llama-3-8b", "mock-aqli", "Generated response text", usage)
                );
            }
        };
        runtime.registerProvider(mockProvider);

        InferencePlan plan = InferencePlan.direct("llama-3-8b", Set.of("text-generation"));
        InferenceRequest request = InferenceRequest.of(plan, "Hello agent world");

        InferenceResult result = runtime.execute(request).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.status()).isEqualTo(InferenceStatus.COMPLETED);
        assertThat(result.output()).isEqualTo("Generated response text");
        assertThat(result.modelId()).contains("llama-3-8b");
        assertThat(result.providerId()).contains("mock-aqli");
        assertThat(result.usage().totalTokens()).isEqualTo(165);
    }

    @Test
    void unknownModel_returnsProviderUnavailable() {
        InferencePlan plan = InferencePlan.direct("unknown-model", Set.of("text-generation"));
        InferenceRequest request = InferenceRequest.of(plan, "Prompt");

        InferenceResult result = runtime.execute(request).toCompletableFuture().join();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.status()).isEqualTo(InferenceStatus.PROVIDER_UNAVAILABLE);
    }

    @Test
    void explicitProviderSelection_routesToSpecifiedProvider() {
        InferenceProviderAdapter providerA = new InferenceProviderAdapter() {
            @Override public String providerId() { return "provider-a"; }
            @Override public boolean supports(String modelId) { return true; }
            @Override public CompletionStage<InferenceResult> execute(InferenceRequest req) {
                return CompletableFuture.completedFuture(
                        InferenceResult.completed("gpt-4o", "provider-a", "From A", InferenceUsage.empty())
                );
            }
        };

        InferenceProviderAdapter providerB = new InferenceProviderAdapter() {
            @Override public String providerId() { return "provider-b"; }
            @Override public boolean supports(String modelId) { return true; }
            @Override public CompletionStage<InferenceResult> execute(InferenceRequest req) {
                return CompletableFuture.completedFuture(
                        InferenceResult.completed("gpt-4o", "provider-b", "From B", InferenceUsage.empty())
                );
            }
        };

        runtime.registerProvider(providerA);
        runtime.registerProvider(providerB);

        InferencePlan plan = InferencePlan.direct("gpt-4o", "provider-b", Set.of("text-generation"));
        InferenceRequest request = InferenceRequest.of(plan, "Prompt");

        InferenceResult result = runtime.execute(request).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.providerId()).contains("provider-b");
        assertThat(result.output()).isEqualTo("From B");
    }

    @Test
    void latencyExceeded_timesOut() {
        InferenceProviderAdapter hangingProvider = new InferenceProviderAdapter() {
            @Override public String providerId() { return "hanging"; }
            @Override public boolean supports(String modelId) { return true; }
            @Override public CompletionStage<InferenceResult> execute(InferenceRequest req) {
                // Returns an uncompleted future that will time out
                return new CompletableFuture<>();
            }
        };
        runtime.registerProvider(hangingProvider);

        InferenceConstraints tightConstraints = new InferenceConstraints(
                1000, 1000, 2000, 50, Optional.empty(), Map.of()
        );
        InferencePlan plan = new InferencePlan(
                "plan-timeout",
                "req-1",
                "slow-model",
                Optional.of("hanging"),
                Set.of(),
                tightConstraints,
                "DIRECT",
                Map.of()
        );
        InferenceRequest request = InferenceRequest.of(plan, "Prompt");

        InferenceResult result = runtime.execute(request).toCompletableFuture().join();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.status()).isEqualTo(InferenceStatus.TIMEOUT);
    }

    @Test
    void providerException_returnsFailedStatus() {
        InferenceProviderAdapter failingProvider = new InferenceProviderAdapter() {
            @Override public String providerId() { return "failing"; }
            @Override public boolean supports(String modelId) { return true; }
            @Override public CompletionStage<InferenceResult> execute(InferenceRequest req) {
                CompletableFuture<InferenceResult> f = new CompletableFuture<>();
                f.completeExceptionally(new RuntimeException("Inference backend connection refused"));
                return f;
            }
        };
        runtime.registerProvider(failingProvider);

        InferencePlan plan = InferencePlan.direct("crash-model", Set.of());
        InferenceRequest request = InferenceRequest.of(plan, "Prompt");

        InferenceResult result = runtime.execute(request).toCompletableFuture().join();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.status()).isEqualTo(InferenceStatus.FAILED);
        assertThat(result.metadata().get("error").toString()).contains("Inference backend connection refused");
    }

    @Test
    void invalidRequest_returnsRequestInvalid() {
        InferenceResult result = runtime.execute(null).toCompletableFuture().join();
        assertThat(result.status()).isEqualTo(InferenceStatus.REQUEST_INVALID);
    }
}
