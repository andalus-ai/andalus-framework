package tech.kayys.wayang.spi.sandbox;

import java.util.Map;
import java.util.Set;

public record SandboxRequest(
        String sandboxId,
        SandboxType preferredType,
        Set<String> requiredFeatures,
        SandboxLimits limits,
        SandboxFilesystem filesystem,
        SandboxNetwork network,
        Map<String, String> environment,
        Map<String, Object> attributes
) {

    public SandboxRequest {
        sandboxId = normalize(sandboxId);

        requiredFeatures = requiredFeatures == null
                ? Set.of()
                : Set.copyOf(requiredFeatures);

        limits = limits == null
                ? SandboxLimits.unlimited()
                : limits;

        filesystem = filesystem == null
                ? SandboxFilesystem.empty()
                : filesystem;

        network = network == null
                ? SandboxNetwork.disabled()
                : network;

        environment = environment == null
                ? Map.of()
                : Map.copyOf(environment);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
