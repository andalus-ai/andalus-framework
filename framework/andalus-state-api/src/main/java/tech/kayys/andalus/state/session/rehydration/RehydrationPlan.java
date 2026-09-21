package tech.kayys.andalus.state.session.rehydration;

import tech.kayys.andalus.state.session.SessionCheckpoint;
import tech.kayys.andalus.state.session.SessionId;

import java.util.List;
import java.util.Objects;

/**
 * Plan describing the ordered reconstruction steps required to resurrect an agent session.
 */
public record RehydrationPlan(
        SessionId sessionId,
        SessionCheckpoint checkpoint,
        List<String> requiredCapabilities,
        List<String> requiredCredentials,
        boolean revalidateAuthority) {

    public RehydrationPlan {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        requiredCapabilities = requiredCapabilities != null ? List.copyOf(requiredCapabilities) : List.of();
        requiredCredentials = requiredCredentials != null ? List.copyOf(requiredCredentials) : List.of();
    }

    public static RehydrationPlan of(SessionId sessionId, SessionCheckpoint checkpoint) {
        return new RehydrationPlan(sessionId, checkpoint, List.of(), List.of(), true);
    }

    public static RehydrationPlan of(
            SessionId sessionId,
            SessionCheckpoint checkpoint,
            List<String> requiredCapabilities,
            List<String> requiredCredentials) {
        return new RehydrationPlan(sessionId, checkpoint, requiredCapabilities, requiredCredentials, true);
    }
}
