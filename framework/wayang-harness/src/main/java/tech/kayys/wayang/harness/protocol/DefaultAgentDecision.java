package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.harness.model.ModelIntent;
import tech.kayys.wayang.tool.resolution.ToolIntent;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a default agent decision.
 *
 * <p>Its components capture `type`, `action`, `reason`, `metadata`.</p>
 *
 * @param type the type
 * @param action the action
 * @param reason the reason
 * @param metadata the metadata
 */


public record DefaultAgentDecision(
        AgentDecisionType type,
        AgentAction action,
        Optional<String> reason,
        Optional<Map<String, Object>> metadata
) implements AgentDecision {

    public DefaultAgentDecision {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
        if (reason == null) reason = Optional.empty();
        if (metadata == null) metadata = Optional.empty();
    }

    public static DefaultAgentDecision infer(ModelIntent intent) {
        return new DefaultAgentDecision(AgentDecisionType.INFER, new InferenceAction(intent), Optional.empty(), Optional.empty());
    }

    public static DefaultAgentDecision tool(ToolIntent intent) {
        return new DefaultAgentDecision(AgentDecisionType.EXECUTE_TOOL, new ToolAction(intent), Optional.empty(), Optional.empty());
    }

    public static DefaultAgentDecision waitFor(WaitCondition condition) {
        return new DefaultAgentDecision(AgentDecisionType.WAIT, new WaitAction(condition), Optional.empty(), Optional.empty());
    }

    public static DefaultAgentDecision askHuman(HumanRequest request) {
        return new DefaultAgentDecision(AgentDecisionType.ASK_HUMAN, new HumanAction(request), Optional.empty(), Optional.empty());
    }

    public static DefaultAgentDecision checkpoint(String label) {
        return new DefaultAgentDecision(AgentDecisionType.CHECKPOINT, CheckpointAction.of(label), Optional.empty(), Optional.empty());
    }

    public static DefaultAgentDecision delegate(AgentRef target, AgentTask task) {
        return new DefaultAgentDecision(AgentDecisionType.DELEGATE, new DelegationAction(target, task), Optional.empty(), Optional.empty());
    }

    public static DefaultAgentDecision complete(String summary, Object result) {
        return new DefaultAgentDecision(AgentDecisionType.COMPLETE, new CompletionAction(AgentOutput.of(summary, result)), Optional.empty(), Optional.empty());
    }

    public static DefaultAgentDecision fail(String reason) {
        return new DefaultAgentDecision(AgentDecisionType.FAIL, new FailureAction(reason), Optional.of(reason), Optional.empty());
    }
}
