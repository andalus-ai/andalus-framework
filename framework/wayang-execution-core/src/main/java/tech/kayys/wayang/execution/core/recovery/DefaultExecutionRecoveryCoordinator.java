package tech.kayys.wayang.execution.core.recovery;

import tech.kayys.wayang.execution.recovery.*;

import java.util.Objects;

public class DefaultExecutionRecoveryCoordinator implements ExecutionRecoveryCoordinator {

    private final RecoveryManager recoveryManager;

    public DefaultExecutionRecoveryCoordinator(RecoveryManager recoveryManager) {
        this.recoveryManager = Objects.requireNonNull(recoveryManager, "recoveryManager cannot be null");
    }

    @Override
    public RecoveryAssessment assess(String executionId) {
        return recoveryManager.assess(executionId);
    }

    @Override
    public RecoveryPlan plan(String executionId, RecoveryAssessment assessment) {
        return recoveryManager.plan(executionId, assessment);
    }

    @Override
    public RecoveryResult execute(RecoveryPlan plan) {
        return recoveryManager.recover(plan);
    }
}
