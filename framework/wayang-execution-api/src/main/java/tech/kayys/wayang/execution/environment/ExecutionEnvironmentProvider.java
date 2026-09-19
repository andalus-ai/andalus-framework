package tech.kayys.wayang.execution.environment;

/**
 * SPI for provisioning and releasing execution environments.
 */
public interface ExecutionEnvironmentProvider {

    ExecutionEnvironment provision(ExecutionEnvironmentRequest request);

    void release(ExecutionEnvironmentId environmentId);
}
