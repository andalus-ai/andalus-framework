package tech.kayys.wayang.harness.protocol;

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
