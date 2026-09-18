package tech.kayys.wayang.harness.scheduling.temporal;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.execution.state.ExecutionState;

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
