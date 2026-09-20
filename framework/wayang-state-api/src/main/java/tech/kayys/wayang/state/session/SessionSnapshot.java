package tech.kayys.wayang.state.session;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Checkpoint payload storing decoupled references rather than raw monolithic state blobs.
 */
public record SessionSnapshot(
    int schemaVersion,
    SessionControlHeader header,
    String contextSnapshotRef,
    String memoryGraphSnapshotRef,
    List<String> artifactRefs,
    Instant capturedAt
) implements Serializable {

    public static final int CURRENT_SCHEMA = 1;

    public SessionSnapshot {
        Objects.requireNonNull(header, "header cannot be null");
        Objects.requireNonNull(capturedAt, "capturedAt cannot be null");
        artifactRefs = artifactRefs != null ? List.copyOf(artifactRefs) : List.of();
    }

    public static SessionSnapshot of(
        SessionControlHeader header,
        String contextSnapshotRef,
        String memoryGraphSnapshotRef,
        List<String> artifactRefs
    ) {
        return new SessionSnapshot(
            CURRENT_SCHEMA,
            header,
            contextSnapshotRef,
            memoryGraphSnapshotRef,
            artifactRefs,
            Instant.now()
        );
    }
}
