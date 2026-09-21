package tech.kayys.andalus.harness.isolation;

import tech.kayys.andalus.harness.semantics.Operation;

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
