package tech.kayys.andalus.execution.turn;

import java.util.List;
import java.util.Optional;

public interface TurnStore {

    void append(AgentTurn turn);

    Optional<AgentTurn> find(
            String executionId,
            TurnId turnId
    );

    List<AgentTurn> findByExecution(
            String executionId
    );

    Optional<AgentTurn> latest(
            String executionId
    );
}
