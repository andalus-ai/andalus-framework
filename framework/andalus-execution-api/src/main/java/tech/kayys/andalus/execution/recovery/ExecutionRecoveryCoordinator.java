package tech.kayys.andalus.execution.recovery;

public interface ExecutionRecoveryCoordinator {
    RecoveryAssessment assess(String executionId);
    RecoveryPlan plan(String executionId, RecoveryAssessment assessment);
    RecoveryResult execute(RecoveryPlan plan);
}
