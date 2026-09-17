package tech.kayys.wayang.harness.execution.state;

import java.util.Objects;

public record ExecutionCursor(
        String phaseId,
        String actionId,
        long sequence
) {
    public ExecutionCursor {
        phaseId = phaseId == null ? "initial" : phaseId;
        actionId = actionId == null ? "none" : actionId;
    }

    public static ExecutionCursor initial() {
        return new ExecutionCursor("initial", "none", 0L);
    }

    public ExecutionCursor next(String phaseId, String actionId) {
        return new ExecutionCursor(phaseId, actionId, sequence + 1);
    }
}
