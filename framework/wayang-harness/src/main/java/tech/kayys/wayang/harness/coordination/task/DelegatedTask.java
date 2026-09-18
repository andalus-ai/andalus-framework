package tech.kayys.wayang.harness.coordination.task;

import tech.kayys.wayang.harness.protocol.AgentRef;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Governed task allocated to an agent within a multi-agent delegation topology.
 */
public record DelegatedTask(
        CoordinationTaskId id,
        String goal,
        Optional<AgentRef> assignedAgent,
        CoordinationTaskState state,
        Map<String, String> contextParameters
) {
    public DelegatedTask {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(goal, "goal");
        assignedAgent = assignedAgent != null ? assignedAgent : Optional.empty();
        state = state != null ? state : CoordinationTaskState.PENDING;
        contextParameters = contextParameters != null ? Map.copyOf(contextParameters) : Map.of();
    }

    public static DelegatedTask of(String goal) {
        return new DelegatedTask(CoordinationTaskId.generate(), goal, Optional.empty(), CoordinationTaskState.PENDING, Map.of());
    }

    public DelegatedTask assignTo(AgentRef agent) {
        return new DelegatedTask(id, goal, Optional.ofNullable(agent), CoordinationTaskState.ASSIGNED, contextParameters);
    }
}
