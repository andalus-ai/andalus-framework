package tech.kayys.andalus.harness.protocol;

import java.util.Objects;

/**
 * Default implementation of {@link DecisionValidator} that enforces structural
 * validity and respects execution {@link DecisionConstraints}.
 */
public final class DefaultDecisionValidator implements DecisionValidator {

    @Override
    public DecisionValidationResult validate(AgentDecision decision, DecisionContext context) {
        if (decision == null) {
            return DecisionValidationResult.rejected("Decision cannot be null");
        }
        if (decision.type() == null) {
            return DecisionValidationResult.rejected("Decision type cannot be null");
        }
        if (decision.action() == null) {
            return DecisionValidationResult.rejected("Decision action cannot be null");
        }

        if (context == null || context.constraints() == null) {
            return DecisionValidationResult.accepted();
        }

        DecisionConstraints constraints = context.constraints();

        switch (decision.type()) {
            case EXECUTE_TOOL -> {
                if (!constraints.allowToolUse()) {
                    return DecisionValidationResult.rejected("Tool execution is disabled by decision constraints");
                }
                if (!(decision.action() instanceof ToolAction)) {
                    return DecisionValidationResult.rejected("Action must be ToolAction for EXECUTE_TOOL");
                }
            }
            case DELEGATE -> {
                if (!constraints.allowDelegation()) {
                    return DecisionValidationResult.rejected("Delegation is disabled by decision constraints");
                }
                if (!(decision.action() instanceof DelegationAction)) {
                    return DecisionValidationResult.rejected("Action must be DelegationAction for DELEGATE");
                }
            }
            case INFER -> {
                if (!(decision.action() instanceof InferenceAction)) {
                    return DecisionValidationResult.rejected("Action must be InferenceAction for INFER");
                }
            }
            case WAIT -> {
                if (!(decision.action() instanceof WaitAction)) {
                    return DecisionValidationResult.rejected("Action must be WaitAction for WAIT");
                }
            }
            case ASK_HUMAN -> {
                if (!(decision.action() instanceof HumanAction)) {
                    return DecisionValidationResult.rejected("Action must be HumanAction for ASK_HUMAN");
                }
            }
            case CHECKPOINT -> {
                if (!(decision.action() instanceof CheckpointAction)) {
                    return DecisionValidationResult.rejected("Action must be CheckpointAction for CHECKPOINT");
                }
            }
            case COMPLETE -> {
                if (!(decision.action() instanceof CompletionAction)) {
                    return DecisionValidationResult.rejected("Action must be CompletionAction for COMPLETE");
                }
            }
            case FAIL -> {
                if (!(decision.action() instanceof FailureAction)) {
                    return DecisionValidationResult.rejected("Action must be FailureAction for FAIL");
                }
            }
        }

        return DecisionValidationResult.accepted();
    }
}
