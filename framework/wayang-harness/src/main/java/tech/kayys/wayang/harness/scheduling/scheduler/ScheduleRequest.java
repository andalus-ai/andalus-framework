package tech.kayys.wayang.harness.scheduling.scheduler;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.scheduling.retry.RetryPolicy;
import tech.kayys.wayang.harness.scheduling.temporal.Deadline;

import java.util.Objects;
import java.util.Optional;

/**
 * Request to schedule an execution wake or delayed run.
 */
public record ScheduleRequest(
        ExecutionId executionId,
        ScheduleSpec specification,
        Optional<Deadline> deadline,
        RetryPolicy retryPolicy
) {
    public ScheduleRequest {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(specification, "specification");
        deadline = deadline != null ? deadline : Optional.empty();
        retryPolicy = retryPolicy != null ? retryPolicy : RetryPolicy.none();
    }

    public static ScheduleRequest of(ExecutionId executionId, ScheduleSpec spec) {
        return new ScheduleRequest(executionId, spec, Optional.empty(), RetryPolicy.none());
    }
}
