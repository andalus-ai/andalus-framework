package tech.kayys.andalus.harness.consistency.recovery;

import tech.kayys.andalus.harness.consistency.state.ExecutionId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Summarises the outcome of a {@link CompensationEngine} run.
 *
 * @param executionId          the execution that was compensated
 * @param stepsAttempted       number of steps for which compensation was attempted
 * @param stepsCompensated     number of steps successfully compensated
 * @param stepsFailed          number of steps whose compensation failed (partial compensation)
 * @param stepsSkipped         number of non-compensatable steps skipped
 * @param overallSuccess       {@code true} when all compensatable steps compensated successfully
 * @param failedStepIds        IDs of steps whose compensation failed (for observability / operator use)
 */
public record CompensationSummary(
        ExecutionId executionId,
        int stepsAttempted,
        int stepsCompensated,
        int stepsFailed,
        int stepsSkipped,
        boolean overallSuccess,
        List<String> failedStepIds
) {
    public CompensationSummary {
        Objects.requireNonNull(executionId, "executionId");
        failedStepIds = failedStepIds == null ? List.of() : List.copyOf(failedStepIds);
    }

    public static Builder builder(ExecutionId executionId) {
        return new Builder(executionId);
    }

    public static class Builder {
        private final ExecutionId executionId;
        private int attempted;
        private int compensated;
        private int failed;
        private int skipped;
        private final List<String> failedIds = new ArrayList<>();

        private Builder(ExecutionId executionId) {
            this.executionId = Objects.requireNonNull(executionId);
        }

        public Builder attempted() { attempted++; return this; }
        public Builder compensated() { compensated++; return this; }
        public Builder failed(String stepId) { failed++; failedIds.add(stepId); return this; }
        public Builder skipped() { skipped++; return this; }

        public CompensationSummary build() {
            return new CompensationSummary(executionId, attempted, compensated, failed, skipped,
                    failed == 0, List.copyOf(failedIds));
        }
    }
}
