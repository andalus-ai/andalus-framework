package tech.kayys.andalus.harness.isolation;

/**
 * Execution environment defining where and under what isolation constraints an operation executes.
 */
public interface ExecutionEnvironment {

    EnvironmentId id();

    EnvironmentType type();

    EnvironmentCapabilities capabilities();

    EnvironmentIsolation isolation();
}
