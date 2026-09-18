package tech.kayys.wayang.spi.sandbox.container;

import tech.kayys.wayang.spi.sandbox.*;

import java.util.Map;
import java.util.Objects;

public record ContainerCreateRequest(
        String image,
        SandboxLimits limits,
        SandboxFilesystem filesystem,
        SandboxNetwork network,
        ContainerSecurityProfile security,
        Map<String, String> environment,
        Map<String, Object> attributes
) {

    public ContainerCreateRequest {
        image = Objects.requireNonNull(image, "image");
        limits = limits == null ? SandboxLimits.unlimited() : limits;
        filesystem = filesystem == null ? SandboxFilesystem.empty() : filesystem;
        network = Objects.requireNonNull(network, "network");
        security = security == null ? ContainerSecurityProfile.hardened() : security;
        environment = environment == null ? Map.of() : Map.copyOf(environment);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
