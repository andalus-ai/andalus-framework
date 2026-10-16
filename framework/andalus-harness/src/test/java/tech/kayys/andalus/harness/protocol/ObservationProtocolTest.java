package tech.kayys.andalus.harness.protocol;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ObservationProtocolTest {

    @Test
    void collectsAndPackagesObservationsIntoAgentTurn() {
        TurnId turnId = TurnId.generate();
        ExecutionId executionId = ExecutionId.generate();

        DefaultObservationCollector collector = new DefaultObservationCollector();
        collector.record(turnId, ObservationType.TOOL_STARTED, "inv-1");
        collector.record(turnId, ObservationType.POLICY_RESULT, "ALLOW");
        collector.record(turnId, ObservationType.TOOL_RESULT, "Found 42 results");

        ObservationSet snapshot = collector.snapshot();
        assertThat(snapshot.size()).isEqualTo(3);
        assertThat(snapshot.ofType(ObservationType.TOOL_RESULT)).hasSize(1);
        assertThat(snapshot.ofType(ObservationType.POLICY_RESULT)).hasSize(1);

        AgentDecision decision = DefaultAgentDecision.fail("Stopped");
        TurnOutcome outcome = TurnOutcome.failure("Stopped");

        AgentTurn turn = new AgentTurn(
                turnId,
                executionId,
                decision,
                snapshot,
                outcome,
                Instant.now()
        );

        assertThat(turn.observations()).isEqualTo(snapshot);
        assertThat(turn.observations().isEmpty()).isFalse();

        // Test backward compatibility legacy constructor
        AgentTurn legacyTurn = new AgentTurn(
                turnId,
                executionId,
                decision,
                outcome,
                Instant.now()
        );
        assertThat(legacyTurn.observations().isEmpty()).isTrue();
    }
}
