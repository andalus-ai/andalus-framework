package tech.kayys.wayang.execution.backend;

import tech.kayys.wayang.execution.environment.ExecutionEnvironment;
import tech.kayys.wayang.execution.process.ProcessDecision;
import tech.kayys.wayang.execution.process.ProcessRequest;

/**
 * Backend interface for executing local OS processes inside an isolated execution environment.
 */
public interface ProcessBackend {

    ProcessDecision validate(ProcessRequest request, ExecutionEnvironment environment);

    int execute(ProcessRequest request, ExecutionEnvironment environment) throws Exception;
}
