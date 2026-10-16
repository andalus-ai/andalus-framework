package tech.kayys.andalus.execution.turn;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AgentTurnTest {

    @Test
    void createsAndStoresTurn() {
        TurnId turnId = TurnId.generate();
        TurnContext context = new TurnContext(
                turnId,
                "exec-123",
                "agent-alpha",
                1L,
                Map.of("step", 1)
        );

        TurnInput input = TurnInput.text("Find latest quarterly report");
        assertTrue(input.hasText());
        assertFalse(input.hasData());

        TurnObservation obs = TurnObservation.of(
                "obs-1",
                ObservationType.TOOL_RESULT,
                "tool.search",
                "Report found: doc-456"
        );

        AgentTurn turn = new AgentTurn(
                turnId,
                "exec-123",
                1L,
                context,
                input,
                List.of(obs),
                "EXECUTE_TOOL",
                "SUCCESS",
                Instant.now(),
                Instant.now(),
                Map.of()
        );

        assertTrue(turn.completed());
        assertEquals(1, turn.observations().size());
        assertEquals(ObservationType.TOOL_RESULT, turn.observations().get(0).type());

        TurnStore store = new InMemoryTurnStore();
        store.append(turn);

        assertTrue(store.find("exec-123", turnId).isPresent());
        assertEquals(turn, store.find("exec-123", turnId).get());
        assertTrue(store.latest("exec-123").isPresent());
        assertEquals(turn, store.latest("exec-123").get());
        assertEquals(List.of(turn), store.findByExecution("exec-123"));
    }
}
