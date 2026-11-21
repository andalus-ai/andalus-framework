package tech.kayys.andalus.execution;

import java.time.Instant;
import java.util.UUID;

/**
 * Snapshot of an execution state.
 */
public final class ExecutionSnapshot {

    private final UUID id;
    private final UUID executionId;
    private final ExecutionState state;
    private final Instant createdAt;

    public ExecutionSnapshot(UUID executionId, ExecutionState state) {
        this(UUID.randomUUID(), executionId, state, Instant.now());
    }

    private ExecutionSnapshot(UUID id, UUID executionId, ExecutionState state, Instant createdAt) {
        this.id = id;
        this.executionId = executionId;
        this.state = state;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getExecutionId() {
        return executionId;
    }

    public ExecutionState getState() {
        return state;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID executionId;
        private ExecutionState state;
        private Instant createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder executionId(UUID executionId) {
            this.executionId = executionId;
            return this;
        }

        public Builder state(ExecutionState state) {
            this.state = state;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExecutionSnapshot build() {
            return new ExecutionSnapshot(
                    id != null ? id : UUID.randomUUID(),
                    executionId,
                    state,
                    createdAt != null ? createdAt : Instant.now());
        }
    }
}
