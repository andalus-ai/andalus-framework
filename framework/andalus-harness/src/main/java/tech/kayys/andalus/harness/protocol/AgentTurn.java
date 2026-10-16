package tech.kayys.andalus.harness.protocol;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Represents an agent turn.
 *
 * <p>Its components capture `id`, `executionId`, `decision`, `observations`, `outcome`, `timestamp`.</p>
 */
public record AgentTurn(
        TurnId id,
        ExecutionId executionId,
        AgentDecision decision,
        ObservationSet observations,
        TurnOutcome outcome,
        Instant timestamp
) {
    public AgentTurn {
        Objects.requireNonNull(id, "TurnId cannot be null");
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        Objects.requireNonNull(decision, "AgentDecision cannot be null");
        observations = observations != null ? observations : new ObservationSet(List.of());
        Objects.requireNonNull(outcome, "TurnOutcome cannot be null");
        if (timestamp == null) timestamp = Instant.now();
    }

    public AgentTurn(
            TurnId id,
            ExecutionId executionId,
            AgentDecision decision,
            TurnOutcome outcome,
            Instant timestamp
    ) {
        this(id, executionId, decision, new ObservationSet(List.of()), outcome, timestamp);
    }

    public static AgentTurn legacy(
            TurnId id,
            ExecutionId executionId,
            AgentDecision decision,
            TurnOutcome outcome,
            Instant timestamp
    ) {
        return new AgentTurn(
                id,
                executionId,
                decision,
                new ObservationSet(List.of()),
                outcome,
                timestamp
        );
    }
}
