package tech.kayys.wayang.execution.recovery;

public interface RecoveryManager {
    RecoveryAssessment assess(String executionId);
    RecoveryPlan plan(String executionId, RecoveryAssessment assessment);
    RecoveryResult recover(RecoveryPlan plan);
}
