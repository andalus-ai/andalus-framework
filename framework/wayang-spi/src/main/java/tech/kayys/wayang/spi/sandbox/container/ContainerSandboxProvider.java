package tech.kayys.wayang.spi.sandbox.container;

import tech.kayys.wayang.spi.sandbox.Sandbox;
import tech.kayys.wayang.spi.sandbox.SandboxProvider;
import tech.kayys.wayang.spi.sandbox.SandboxRequest;
import tech.kayys.wayang.spi.sandbox.SandboxType;

public interface ContainerSandboxProvider extends SandboxProvider {

    @Override
    default Sandbox create(SandboxRequest request) throws Exception {
        if (request.preferredType() != null
                && request.preferredType() != SandboxType.CONTAINER
                && request.preferredType() != SandboxType.NONE) {

            throw new IllegalArgumentException(
                    "ContainerSandboxProvider requires SandboxType.CONTAINER or NONE");
        }

        return createContainer(ContainerSandboxRequest.from(request));
    }

    ContainerSandbox createContainer(ContainerSandboxRequest request) throws Exception;
}
