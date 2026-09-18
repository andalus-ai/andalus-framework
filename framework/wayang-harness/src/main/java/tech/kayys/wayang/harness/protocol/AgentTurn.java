package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a agent turn.
 *
 * <p>Its components capture `id`, `execution id`, `decision`, `outcome`, `timestamp`.</p>
 *
 * @param id the id
 * @param executionId the execution id
 * @param decision the decision
 * @param outcome the outcome
 * @param timestamp the timestamp
 */


public record AgentTurn(
        TurnId id,
        ExecutionId executionId,
        AgentDecision decision,
        TurnOutcome outcome,
        Instant timestamp
) {
    public AgentTurn {
        Objects.requireNonNull(id, "TurnId cannot be null");
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        Objects.requireNonNull(decision, "AgentDecision cannot be null");
        Objects.requireNonNull(outcome, "TurnOutcome cannot be null");
        if (timestamp == null) timestamp = Instant.now();
    }
}
