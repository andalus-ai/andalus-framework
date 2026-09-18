package tech.kayys.wayang.spi.sandbox.container;

import tech.kayys.wayang.spi.sandbox.*;

import java.util.Map;
import java.util.Objects;

public record ContainerSandboxRequest(
        String sandboxId,
        String image,
        SandboxLimits limits,
        SandboxFilesystem filesystem,
        SandboxNetwork network,
        Map<String, String> environment,
        Map<String, Object> attributes
) {

    public ContainerSandboxRequest {
        sandboxId = Objects.requireNonNull(sandboxId, "sandboxId");
        image = Objects.requireNonNull(image, "image");

        if (image.isBlank()) {
            throw new IllegalArgumentException("image must not be blank");
        }

        limits = limits == null ? SandboxLimits.unlimited() : limits;
        filesystem = filesystem == null ? SandboxFilesystem.empty() : filesystem;
        network = network == null ? SandboxNetwork.disabled() : network;
        environment = environment == null ? Map.of() : Map.copyOf(environment);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static ContainerSandboxRequest from(SandboxRequest request) {
        Objects.requireNonNull(request, "request");

        Object image = request.attributes().get("container.image");
        if (!(image instanceof String value) || value.isBlank()) {
            throw new IllegalArgumentException("container.image is required in attributes");
        }

        return new ContainerSandboxRequest(
                request.sandboxId() != null ? request.sandboxId() : java.util.UUID.randomUUID().toString(),
                value,
                request.limits(),
                request.filesystem(),
                request.network(),
                request.environment(),
                request.attributes()
        );
    }
}
