package tech.kayys.wayang.harness.protocol;

/**
 * Defines the contract for agent action operations in the Wayang framework.
 */


public sealed interface AgentAction
        permits
        InferenceAction,
        ToolAction,
        WaitAction,
        HumanAction,
        CheckpointAction,
        DelegationAction,
        CompletionAction,
        FailureAction {
}
