package tech.kayys.wayang.execution.replay;

import java.util.Objects;

/**
 * Manifest defining execution state and parameters required to inspect, simulate, or replay.
 */
public record ReplayManifest(
        String sourceExecutionId,
        long stateSequence,
        String graphVersion,
        ReplayMode mode,
        ReplaySafety safety
) {
    public ReplayManifest {
        Objects.requireNonNull(sourceExecutionId, "sourceExecutionId cannot be null");
        graphVersion = graphVersion == null ? "v1" : graphVersion;
        if (mode == null) {
            mode = ReplayMode.SIMULATE;
        }
        if (safety == null) {
            safety = ReplaySafety.SAFE;
        }
    }

    public static ReplayManifest simulation(String executionId, long sequence) {
        return new ReplayManifest(executionId, sequence, "v1", ReplayMode.SIMULATE, ReplaySafety.SIMULATED);
    }

    public static ReplayManifest inspect(String executionId) {
        return new ReplayManifest(executionId, 1L, "v1", ReplayMode.INSPECT, ReplaySafety.SAFE);
    }
}
