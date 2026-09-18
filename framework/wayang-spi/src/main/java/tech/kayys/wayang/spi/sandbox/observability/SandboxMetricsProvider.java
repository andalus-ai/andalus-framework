package tech.kayys.wayang.spi.sandbox.observability;

public interface SandboxMetricsProvider {

    boolean supports(String providerId);

    SandboxMetricsSnapshot collect(
            String sandboxId)
            throws Exception;
}
