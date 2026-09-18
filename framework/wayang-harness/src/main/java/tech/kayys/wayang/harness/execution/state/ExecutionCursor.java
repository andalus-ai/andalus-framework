package tech.kayys.wayang.harness.execution.state;

import java.util.Objects;

/**
 * Represents a execution cursor.
 *
 * <p>Its components capture `phase id`, `action id`, `sequence`.</p>
 *
 * @param phaseId the phase id
 * @param actionId the action id
 * @param sequence the sequence
 */


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
