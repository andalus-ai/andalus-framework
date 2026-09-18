package tech.kayys.wayang.spi.sandbox.observability;

public interface SandboxObservability {

    void recordEvent(SandboxObservation observation);

    void recordMetrics(SandboxMetricsSnapshot metrics);

    void recordFailure(SandboxFailure failure);
}
