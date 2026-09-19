package tech.kayys.wayang.execution.snapshot;

/**
 * Evaluates whether a sandbox state snapshot may be created and what data may be included.
 */
public interface SnapshotPolicy {

    SnapshotDecision evaluate(SnapshotRequest request);

    static SnapshotPolicy disallowSecrets() {
        return request -> {
            if (request.includeSecrets()) {
                return SnapshotDecision.deny("Secrets cannot be captured in snapshots");
            }
            return SnapshotDecision.allow("Snapshot permitted without secrets");
        };
    }
}
