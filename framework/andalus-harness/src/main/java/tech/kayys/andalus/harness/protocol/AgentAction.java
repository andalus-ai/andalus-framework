package tech.kayys.andalus.harness.protocol;

/**
 * Defines the contract for agent action operations in the Andalus framework.
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
