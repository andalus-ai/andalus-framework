
package tech.kayys.andalus.agent.kernel.execution;

import tech.kayys.andalus.agent.kernel.interaction.InteractionBus;
import tech.kayys.andalus.agent.kernel.interaction.InteractionEventType;

import java.util.Map;
import java.util.Objects;

public final class AgentExecutionController {

    private final AgentExecutionStateStore stateStore;
    private final InteractionBus interactionBus;

    public AgentExecutionController(
            AgentExecutionStateStore stateStore,
            InteractionBus interactionBus
    ) {
        this.stateStore =
                Objects.requireNonNull(stateStore);

        this.interactionBus =
                Objects.requireNonNull(interactionBus);
    }

    public AgentExecutionState start(
            String runId
    ) {
        return transition(
                runId,
                AgentExecutionState.RUNNING
        );
    }

    public AgentExecutionState waitForApproval(
            String runId,
            String approvalId
    ) {
        AgentExecutionState state =
                transition(
                        runId,
                        AgentExecutionState.WAITING_FOR_APPROVAL
                );

        interactionBus.publish(
                runId,
                InteractionEventType.PAUSED,
                Map.of(
                        "reason",
                        "approval-required",
                        "approvalId",
                        approvalId
                )
        );

        return state;
    }

    public AgentExecutionState waitForInput(
            String runId,
            String inputId
    ) {
        AgentExecutionState state =
                transition(
                        runId,
                        AgentExecutionState.WAITING_FOR_INPUT
                );

        interactionBus.publish(
                runId,
                InteractionEventType.PAUSED,
                Map.of(
                        "reason",
                        "input-required",
                        "inputId",
                        inputId
                )
        );

        return state;
    }

    public AgentExecutionState pause(
            String runId,
            String reason
    ) {
        AgentExecutionState state =
                transition(
                        runId,
                        AgentExecutionState.PAUSED
                );

        interactionBus.publish(
                runId,
                InteractionEventType.PAUSED,
                Map.of(
                        "reason",
                        reason
                )
        );

        return state;
    }

    public AgentExecutionState resume(
            String runId
    ) {
        transition(
                runId,
                AgentExecutionState.RESUMING
        );

        AgentExecutionState state =
                transition(
                        runId,
                        AgentExecutionState.RUNNING
                );

        interactionBus.publish(
                runId,
                InteractionEventType.RESUMED,
                Map.of()
        );

        return state;
    }

    public AgentExecutionState complete(
            String runId
    ) {
        return transition(
                runId,
                AgentExecutionState.COMPLETED
        );
    }

    public AgentExecutionState fail(
            String runId,
            String reason
    ) {
        interactionBus.publish(
                runId,
                InteractionEventType.CANCELLED,
                Map.of(
                        "reason",
                        reason
                )
        );

        return transition(
                runId,
                AgentExecutionState.FAILED
        );
    }

    public AgentExecutionState cancel(
            String runId
    ) {
        AgentExecutionState state =
                transition(
                        runId,
                        AgentExecutionState.CANCELLED
                );

        interactionBus.publish(
                runId,
                InteractionEventType.CANCELLED,
                Map.of()
        );

        return state;
    }

    public AgentExecutionState state(
            String runId
    ) {
        return stateStore.get(runId);
    }

    private AgentExecutionState transition(
            String runId,
            AgentExecutionState next
    ) {
        Objects.requireNonNull(runId);
        Objects.requireNonNull(next);

        return stateStore.set(
                runId,
                next
        );
    }
}
