package tech.kayys.andalus.harness.fabric;

/** Snapshot returned when a worker enters or completes graceful drain. */
public record WorkerDrainResult(WorkerId workerId, boolean draining, boolean drained, int activeAssignments) {
    public WorkerDrainResult {
        if (workerId == null) throw new IllegalArgumentException("workerId cannot be null");
        if (activeAssignments < 0) throw new IllegalArgumentException("activeAssignments cannot be negative");
        if (drained && (!draining || activeAssignments != 0)) {
            throw new IllegalArgumentException("A drained worker must be draining with no active assignments");
        }
    }
}
