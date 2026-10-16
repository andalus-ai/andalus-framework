
package tech.kayys.andalus.agent.kernel.execution;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryAgentExecutionStateStore
        implements AgentExecutionStateStore {

    private final Map<String, AgentExecutionState> states =
            new ConcurrentHashMap<>();

    @Override
    public AgentExecutionState get(String runId) {
        return states.get(runId);
    }

    @Override
    public AgentExecutionState set(
            String runId,
            AgentExecutionState state
    ) {
        states.put(runId, state);
        return state;
    }
}
