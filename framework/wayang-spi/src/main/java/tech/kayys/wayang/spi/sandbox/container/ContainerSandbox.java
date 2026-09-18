package tech.kayys.wayang.spi.sandbox.container;

import tech.kayys.wayang.spi.sandbox.Sandbox;

public interface ContainerSandbox extends Sandbox {

    String containerId();

    String image();

    ContainerRuntime runtime();

    void execute(ContainerExecutionRequest request) throws Exception;
}
