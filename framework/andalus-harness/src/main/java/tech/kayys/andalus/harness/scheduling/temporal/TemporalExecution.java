package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionState;

import java.time.Instant;
import java.util.Optional;

/**
 * Representation of an execution whose lifetime is temporally managed and durable.
 */
public interface TemporalExecution {

    ExecutionId executionId();

    ExecutionState state();

    Optional<WakeCondition> wakeCondition();

    Optional<Deadline> deadline();

    Optional<Instant> nextWakeAt();
}
