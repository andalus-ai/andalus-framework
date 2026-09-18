package tech.kayys.wayang.spi.sandbox.vm;

import tech.kayys.wayang.spi.sandbox.*;

import java.util.Map;
import java.util.Objects;

public record VMSandboxRequest(
        String sandboxId,
        String image,
        SandboxLimits limits,
        SandboxFilesystem filesystem,
        SandboxNetwork network,
        Map<String, String> environment,
        Map<String, Object> attributes
) {

    public VMSandboxRequest {
        sandboxId = Objects.requireNonNull(sandboxId, "sandboxId");
        image = Objects.requireNonNull(image, "image");
        limits = limits == null ? SandboxLimits.unlimited() : limits;
        filesystem = filesystem == null ? SandboxFilesystem.empty() : filesystem;
        network = network == null ? SandboxNetwork.disabled() : network;
        environment = environment == null ? Map.of() : Map.copyOf(environment);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static VMSandboxRequest from(SandboxRequest request) {
        Objects.requireNonNull(request, "request");
        Object image = request.attributes().get("vm.image");
        if (!(image instanceof String value) || value.isBlank()) {
            throw new IllegalArgumentException("vm.image is required in attributes");
        }

        return new VMSandboxRequest(
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
