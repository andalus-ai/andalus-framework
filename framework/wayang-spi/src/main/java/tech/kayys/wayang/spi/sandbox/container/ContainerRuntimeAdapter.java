package tech.kayys.wayang.spi.sandbox.container;

public interface ContainerRuntimeAdapter {

    ContainerRuntime runtime();

    ContainerHandle create(ContainerCreateRequest request) throws Exception;

    void start(ContainerHandle container) throws Exception;

    void stop(ContainerHandle container) throws Exception;

    void destroy(ContainerHandle container) throws Exception;

    ContainerExecution execute(ContainerHandle container, ContainerExecutionRequest request) throws Exception;
}
