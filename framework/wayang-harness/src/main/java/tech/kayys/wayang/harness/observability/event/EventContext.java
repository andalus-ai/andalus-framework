package tech.kayys.wayang.harness.observability.event;

import tech.kayys.wayang.harness.artifact.ArtifactId;
import tech.kayys.wayang.harness.environment.v3.resource.LeaseId;
import tech.kayys.wayang.harness.execution.ActionId;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.protocol.AgentRef;
import tech.kayys.wayang.harness.protocol.TurnId;
import tech.kayys.wayang.harness.workspace.WorkspaceId;

import java.util.Objects;
import java.util.Optional;

/**
 * Contextual correlation metadata linking an event to execution, turn, action, resource, workspace, and causal parent.
 */
public record EventContext(
        ExecutionId executionId,
        Optional<TurnId> turnId,
        Optional<ActionId> actionId,
        Optional<LeaseId> leaseId,
        Optional<WorkspaceId> workspaceId,
        Optional<ArtifactId> artifactId,
        Optional<AgentRef> agentRef,
        Optional<EventId> parentEventId
) {
    public EventContext {
        Objects.requireNonNull(executionId, "executionId");
        turnId = turnId != null ? turnId : Optional.empty();
        actionId = actionId != null ? actionId : Optional.empty();
        leaseId = leaseId != null ? leaseId : Optional.empty();
        workspaceId = workspaceId != null ? workspaceId : Optional.empty();
        artifactId = artifactId != null ? artifactId : Optional.empty();
        agentRef = agentRef != null ? agentRef : Optional.empty();
        parentEventId = parentEventId != null ? parentEventId : Optional.empty();
    }

    public static EventContext ofExecution(ExecutionId executionId) {
        return new EventContext(executionId, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static EventContext of(ExecutionId executionId, TurnId turnId, ActionId actionId) {
        return new EventContext(executionId, Optional.ofNullable(turnId), Optional.ofNullable(actionId), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public EventContext withParent(EventId parent) {
        return new EventContext(executionId, turnId, actionId, leaseId, workspaceId, artifactId, agentRef, Optional.ofNullable(parent));
    }
}
