package tech.kayys.wayang.execution.recovery;

import tech.kayys.wayang.execution.checkpoint.ExecutionCheckpoint;

import java.util.Optional;

public record RecoveryAssessment(
        String executionId,
        boolean recoverable,
        RecoveryMode recommendedMode,
        Optional<ExecutionCheckpoint> latestCheckpoint,
        String explanation
) {}
