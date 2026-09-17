package tech.kayys.wayang.harness.context;

import java.util.Optional;

/**
 * Tracks session continuity, parent linkage, and conversational versioning.
 */
public interface HarnessSession {

    String sessionId();

    Optional<String> parentSessionId();

    long version();

    SessionState state();
}
