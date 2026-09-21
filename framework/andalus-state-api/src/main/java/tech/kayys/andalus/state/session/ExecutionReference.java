package tech.kayys.andalus.state.session;

/**
 * Decoupled reference to a physical execution instance of an agent session.
 * The session knows its executions; executions do not own the session.
 */
public record ExecutionReference(
        String executionId,
        String latestAttemptId,
        String state) {

    public static ExecutionReference of(String executionId, String latestAttemptId, String state) {
        return new ExecutionReference(executionId, latestAttemptId, state);
    }
}
