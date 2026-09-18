package tech.kayys.wayang.harness.isolation;

import tech.kayys.wayang.harness.semantics.Operation;

import java.util.List;

/**
 * Strategy interface for selecting and allocating an execution environment that fulfills
 * an operation's constraints and resource request.
 */
public interface EnvironmentResolver {

    EnvironmentResolution resolve(
            Operation operation,
            ResourceRequest resources,
            List<ExecutionEnvironment> candidateEnvironments
    );
}
