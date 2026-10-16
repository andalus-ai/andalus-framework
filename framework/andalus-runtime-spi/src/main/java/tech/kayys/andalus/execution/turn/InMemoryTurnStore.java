package tech.kayys.andalus.execution.turn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe in-memory implementation of TurnStore.
 */
public class InMemoryTurnStore implements TurnStore {

    private final Map<String, List<AgentTurn>> turnsByExecution = new ConcurrentHashMap<>();

    @Override
    public void append(AgentTurn turn) {
        Objects.requireNonNull(turn, "turn cannot be null");
        turnsByExecution
                .computeIfAbsent(turn.executionId(), k -> new CopyOnWriteArrayList<>())
                .add(turn);
    }

    @Override
    public Optional<AgentTurn> find(String executionId, TurnId turnId) {
        if (executionId == null || turnId == null) {
            return Optional.empty();
        }
        List<AgentTurn> turns = turnsByExecution.get(executionId);
        if (turns == null) {
            return Optional.empty();
        }
        return turns.stream()
                .filter(t -> t.id().equals(turnId))
                .findFirst();
    }

    @Override
    public List<AgentTurn> findByExecution(String executionId) {
        if (executionId == null) {
            return List.of();
        }
        List<AgentTurn> turns = turnsByExecution.get(executionId);
        return turns == null ? List.of() : List.copyOf(turns);
    }

    @Override
    public Optional<AgentTurn> latest(String executionId) {
        if (executionId == null) {
            return Optional.empty();
        }
        List<AgentTurn> turns = turnsByExecution.get(executionId);
        if (turns == null || turns.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(turns.get(turns.size() - 1));
    }
}
