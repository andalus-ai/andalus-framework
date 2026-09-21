package tech.kayys.andalus.spi.sandbox.observability;

import java.util.Optional;

public interface SandboxObservabilityManager {

    void record(SandboxObservation observation);

    void recordMetrics(SandboxMetricsSnapshot metrics);

    void recordFailure(SandboxFailure failure);

    Optional<SandboxHealth> health(
            String sandboxId);
}
