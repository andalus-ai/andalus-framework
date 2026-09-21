package tech.kayys.andalus.execution.backend;

import tech.kayys.andalus.execution.environment.ExecutionEnvironment;
import tech.kayys.andalus.execution.process.ProcessDecision;
import tech.kayys.andalus.execution.process.ProcessRequest;

/**
 * Backend interface for executing local OS processes inside an isolated execution environment.
 */
public interface ProcessBackend {

    ProcessDecision validate(ProcessRequest request, ExecutionEnvironment environment);

    int execute(ProcessRequest request, ExecutionEnvironment environment) throws Exception;
}
