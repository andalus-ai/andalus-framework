package tech.kayys.andalus.state.session;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Checkpoint contract capturing the semantic continuity of an agent session
 * across executions and worker restarts.
 */
public record SessionCheckpoint(
        SessionId session,
        SessionVersion version,
        WorkflowReference workflow,
        List<ExecutionReference> executions,
        List<String> contextReferences,
        List<String> memoryReferences,
        List<String> artifactReferences,
        String provenanceReference,
        Instant createdAt) {

    public SessionCheckpoint {
        Objects.requireNonNull(session, "session must not be null");
        Objects.requireNonNull(version, "version must not be null");
        executions = executions != null ? List.copyOf(executions) : List.of();
        contextReferences = contextReferences != null ? List.copyOf(contextReferences) : List.of();
        memoryReferences = memoryReferences != null ? List.copyOf(memoryReferences) : List.of();
        artifactReferences = artifactReferences != null ? List.copyOf(artifactReferences) : List.of();
        createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static SessionCheckpoint of(
            SessionId session,
            SessionVersion version,
            WorkflowReference workflow,
            List<ExecutionReference> executions,
            List<String> contextReferences,
            List<String> memoryReferences,
            List<String> artifactReferences,
            String provenanceReference) {
        return new SessionCheckpoint(
                session,
                version,
                workflow,
                executions,
                contextReferences,
                memoryReferences,
                artifactReferences,
                provenanceReference,
                Instant.now());
    }
}
