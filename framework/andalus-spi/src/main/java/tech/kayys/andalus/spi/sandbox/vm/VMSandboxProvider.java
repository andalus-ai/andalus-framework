package tech.kayys.andalus.spi.sandbox.vm;

import tech.kayys.andalus.spi.sandbox.Sandbox;
import tech.kayys.andalus.spi.sandbox.SandboxProvider;
import tech.kayys.andalus.spi.sandbox.SandboxRequest;
import tech.kayys.andalus.spi.sandbox.SandboxType;

public interface VMSandboxProvider extends SandboxProvider {

    @Override
    default Sandbox create(SandboxRequest request) throws Exception {
        if (request.preferredType() != null
                && request.preferredType() != SandboxType.VM
                && request.preferredType() != SandboxType.NONE) {

            throw new IllegalArgumentException("VM provider requires SandboxType.VM or NONE");
        }

        return createVm(VMSandboxRequest.from(request));
    }

    VMSandbox createVm(VMSandboxRequest request) throws Exception;
}
