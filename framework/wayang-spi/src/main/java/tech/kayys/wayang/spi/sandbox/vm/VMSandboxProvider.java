package tech.kayys.wayang.spi.sandbox.vm;

import tech.kayys.wayang.spi.sandbox.Sandbox;
import tech.kayys.wayang.spi.sandbox.SandboxProvider;
import tech.kayys.wayang.spi.sandbox.SandboxRequest;
import tech.kayys.wayang.spi.sandbox.SandboxType;

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
