package tech.kayys.wayang.harness.environment.v3.process;

/**
 * Runtime SPI for spawning and managing system processes under governance.
 */
public interface ProcessRuntime {

    ProcessExecutionHandle spawn(ProcessRequest request);
}
