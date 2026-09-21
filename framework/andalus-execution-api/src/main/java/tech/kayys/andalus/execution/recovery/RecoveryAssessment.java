package tech.kayys.andalus.execution.recovery;

import tech.kayys.andalus.execution.checkpoint.ExecutionCheckpoint;

import java.util.Optional;

public record RecoveryAssessment(
        String executionId,
        boolean recoverable,
        RecoveryMode recommendedMode,
        Optional<ExecutionCheckpoint> latestCheckpoint,
        String explanation
) {}
