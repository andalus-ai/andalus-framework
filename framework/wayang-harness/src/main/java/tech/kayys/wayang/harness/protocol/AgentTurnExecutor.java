package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.harness.execution.checkpoint.Checkpoint;
import tech.kayys.wayang.harness.execution.checkpoint.CheckpointContext;
import tech.kayys.wayang.harness.execution.checkpoint.CheckpointId;
import tech.kayys.wayang.harness.execution.checkpoint.CheckpointManager;
import tech.kayys.wayang.harness.model.GovernedModelExecutor;
import tech.kayys.wayang.harness.model.ModelExecutionContext;
import tech.kayys.wayang.harness.model.ModelResult;
import tech.kayys.wayang.harness.tool.GovernedToolExecutor;
import tech.kayys.wayang.harness.tool.ToolExecutionContext;
import tech.kayys.wayang.harness.tool.ToolResult;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class AgentTurnExecutor {

    private final GovernedModelExecutor modelExecutor;
    private final GovernedToolExecutor toolExecutor;
    private final CheckpointManager checkpointManager;

    public AgentTurnExecutor(
            GovernedModelExecutor modelExecutor,
            GovernedToolExecutor toolExecutor,
            CheckpointManager checkpointManager
    ) {
        this.modelExecutor = modelExecutor;
        this.toolExecutor = toolExecutor;
        this.checkpointManager = checkpointManager;
    }

    public AgentTurn executeTurn(
            AgentTurnContext turnContext,
            AgentDecision decision,
            ModelExecutionContext modelContext,
            ToolExecutionContext toolContext
    ) {
        Objects.requireNonNull(turnContext, "turnContext cannot be null");
        Objects.requireNonNull(decision, "decision cannot be null");

        TurnId turnId = TurnId.generate();
        TurnOutcome outcome;

        switch (decision.type()) {
            case INFER -> {
                if (modelExecutor == null) {
                    outcome = TurnOutcome.failure("No model executor configured");
                } else if (decision.action() instanceof InferenceAction inf) {
                    ModelResult res = modelExecutor.execute(inf.intent(), modelContext);
                    outcome = res.isSuccess() ? TurnOutcome.success(res) : TurnOutcome.failure("Model error: " + res.output().text());
                } else {
                    outcome = TurnOutcome.failure("Invalid action type for INFER");
                }
            }
            case EXECUTE_TOOL -> {
                if (toolExecutor == null) {
                    outcome = TurnOutcome.failure("No tool executor configured");
                } else if (decision.action() instanceof ToolAction tool) {
                    ToolResult res = toolExecutor.execute(tool.intent(), toolContext, null);
                    outcome = res.isSuccess() ? TurnOutcome.success(res) : TurnOutcome.failure("Tool error: " + res.output().preview());
                } else {
                    outcome = TurnOutcome.failure("Invalid action type for EXECUTE_TOOL");
                }
            }
            case WAIT -> {
                outcome = TurnOutcome.success("Waiting condition accepted");
            }
            case ASK_HUMAN -> {
                outcome = TurnOutcome.success("Human approval requested");
            }
            case CHECKPOINT -> {
                if (checkpointManager != null && decision.action() instanceof CheckpointAction cp) {
                    tech.kayys.wayang.harness.execution.state.ExecutionState execState =
                            new tech.kayys.wayang.harness.execution.state.DefaultExecutionState(
                                    turnContext.executionId(),
                                    tech.kayys.wayang.harness.execution.state.ExecutionStatus.RUNNING,
                                    1L,
                                    null,
                                    tech.kayys.wayang.harness.execution.state.ExecutionCursor.initial(),
                                    tech.kayys.wayang.harness.execution.state.ExecutionData.empty()
                            );
                    CheckpointId createdId = checkpointManager.create(execState);
                    outcome = TurnOutcome.success(createdId);
                } else {
                    outcome = TurnOutcome.success("Checkpoint completed");
                }
            }
            case DELEGATE -> {
                outcome = TurnOutcome.success("Delegation initiated");
            }
            case COMPLETE -> {
                if (decision.action() instanceof CompletionAction comp) {
                    outcome = TurnOutcome.success(comp.output());
                } else {
                    outcome = TurnOutcome.success("Completed");
                }
            }
            case FAIL -> {
                String reason = decision.reason().orElse("Agent explicitly declared failure");
                outcome = TurnOutcome.failure(reason);
            }
            default -> outcome = TurnOutcome.failure("Unknown decision type: " + decision.type());
        }

        return new AgentTurn(turnId, turnContext.executionId(), decision, outcome, Instant.now());
    }
}
