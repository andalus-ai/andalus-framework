package tech.kayys.andalus.spi.sandbox.container;

import tech.kayys.andalus.spi.sandbox.Sandbox;

public interface ContainerSandbox extends Sandbox {

    String containerId();

    String image();

    ContainerRuntime runtime();

    void execute(ContainerExecutionRequest request) throws Exception;
}
