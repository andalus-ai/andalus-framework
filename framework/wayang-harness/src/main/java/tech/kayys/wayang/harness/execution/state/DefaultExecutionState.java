package tech.kayys.wayang.harness.execution.state;

import tech.kayys.wayang.harness.context.HarnessContext;

import java.util.Objects;

public record DefaultExecutionState(
        ExecutionId executionId,
        ExecutionStatus status,
        long version,
        HarnessContext context,
        ExecutionCursor cursor,
        ExecutionData data
) implements ExecutionState {

    public DefaultExecutionState {
        Objects.requireNonNull(executionId, "executionId");
        status = status == null ? ExecutionStatus.CREATED : status;
        cursor = cursor == null ? ExecutionCursor.initial() : cursor;
        data = data == null ? ExecutionData.empty() : data;
    }

    public DefaultExecutionState withStatus(ExecutionStatus newStatus) {
        return new DefaultExecutionState(executionId, newStatus, version + 1, context, cursor, data);
    }

    public DefaultExecutionState withCursor(ExecutionCursor newCursor) {
        return new DefaultExecutionState(executionId, status, version + 1, context, newCursor, data);
    }

    public DefaultExecutionState withData(ExecutionData newData) {
        return new DefaultExecutionState(executionId, status, version + 1, context, cursor, newData);
    }
}
