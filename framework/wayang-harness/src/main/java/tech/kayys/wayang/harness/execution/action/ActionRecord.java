package tech.kayys.wayang.harness.execution.action;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public record ActionRecord(
        ActionId id,
        ExecutionId executionId,
        String type,
        ActionStatus status,
        ActionExecutionMode mode,
        Instant startedAt,
        Optional<Instant> completedAt,
        Optional<String> output,
        Optional<String> error
) {
    public ActionRecord {
        id = id == null ? ActionId.generate() : id;
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(type, "type");
        status = status == null ? ActionStatus.STARTED : status;
        mode = mode == null ? ActionExecutionMode.IDEMPOTENT : mode;
        startedAt = startedAt == null ? Instant.now() : startedAt;
        completedAt = completedAt == null ? Optional.empty() : completedAt;
        output = output == null ? Optional.empty() : output;
        error = error == null ? Optional.empty() : error;
    }

    public static ActionRecord started(ActionId id, ExecutionId executionId, String type, ActionExecutionMode mode) {
        return new ActionRecord(id, executionId, type, ActionStatus.STARTED, mode, Instant.now(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public ActionRecord complete(String output) {
        return new ActionRecord(id, executionId, type, ActionStatus.COMPLETED, mode, startedAt, Optional.of(Instant.now()), Optional.ofNullable(output), Optional.empty());
    }

    public ActionRecord fail(String error) {
        return new ActionRecord(id, executionId, type, ActionStatus.FAILED, mode, startedAt, Optional.of(Instant.now()), Optional.empty(), Optional.ofNullable(error));
    }
}
