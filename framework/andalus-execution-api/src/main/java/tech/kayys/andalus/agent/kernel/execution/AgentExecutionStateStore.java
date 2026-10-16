
package tech.kayys.andalus.agent.kernel.execution;

import java.util.Optional;

public interface AgentExecutionStateStore {

    AgentExecutionState get(String runId);

    AgentExecutionState set(
            String runId,
            AgentExecutionState state
    );

    default Optional<AgentExecutionState> find(String runId) {
        return Optional.ofNullable(get(runId));
    }
}
