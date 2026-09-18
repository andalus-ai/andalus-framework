package tech.kayys.wayang.spi.sandbox;

/**
 * Provider SPI capable of creating isolated sandboxes.
 */
public interface SandboxProvider {

    default String providerId() {
        return getProviderId();
    }

    default String getProviderId() {
        return descriptor() != null ? descriptor().id() : "default";
    }

    default SandboxProviderDescriptor descriptor() {
        return null;
    }

    default Sandbox create(SandboxRequest request) throws Exception {
        return createSandbox(null);
    }

    /**
     * Backward-compatible creation method using legacy SandboxConfiguration.
     */
    default Sandbox createSandbox(SandboxConfiguration config) throws Exception {
        return create(new SandboxRequest(
                null,
                SandboxType.NONE,
                java.util.Set.of(),
                SandboxLimits.unlimited(),
                SandboxFilesystem.empty(),
                SandboxNetwork.disabled(),
                config != null && config.getEnvironmentVariables() != null
                        ? config.getEnvironmentVariables()
                        : java.util.Map.of(),
                java.util.Map.of()
        ));
    }
}
