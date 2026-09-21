package tech.kayys.andalus.execution.core.control;

import tech.kayys.andalus.execution.control.ControlCommand;
import tech.kayys.andalus.execution.control.ControlEvent;
import tech.kayys.andalus.execution.control.ControlResult;
import tech.kayys.andalus.execution.control.ExecutionController;
import tech.kayys.andalus.execution.control.ExecutionLifecycleState;
import tech.kayys.andalus.execution.control.ExecutionTransitionPolicy;
import tech.kayys.andalus.execution.control.TransitionDecision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe implementation of {@link ExecutionController} applying commands and tracking state.
 */
public class DefaultExecutionController implements ExecutionController {

    private final ExecutionTransitionPolicy transitionPolicy;
    private final Map<String, ExecutionLifecycleState> executionStates = new ConcurrentHashMap<>();
    private final Map<String, List<ControlEvent>> eventHistory = new ConcurrentHashMap<>();

    public DefaultExecutionController(ExecutionTransitionPolicy transitionPolicy) {
        this.transitionPolicy = Objects.requireNonNull(transitionPolicy, "transitionPolicy cannot be null");
    }

    public void registerExecution(String executionId, ExecutionLifecycleState initialState) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        executionStates.put(executionId, initialState != null ? initialState : ExecutionLifecycleState.CREATED);
    }

    @Override
    public synchronized ControlResult apply(String executionId, ControlCommand command) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(command, "command cannot be null");

        ExecutionLifecycleState currentState = executionStates.getOrDefault(executionId, ExecutionLifecycleState.CREATED);
        TransitionDecision decision = transitionPolicy.evaluate(currentState, command.action(), command.parameters());

        if (!decision.allowed()) {
            return ControlResult.failure(executionId, command.action(), "Transition rejected: " + decision.reason());
        }

        ExecutionLifecycleState nextState = decision.targetState();
        executionStates.put(executionId, nextState);

        ControlEvent event = ControlEvent.of(executionId, currentState, nextState, command.action());
        eventHistory.computeIfAbsent(executionId, k -> Collections.synchronizedList(new ArrayList<>())).add(event);

        return ControlResult.success(executionId, command.action(), "Transitioned from " + currentState + " to " + nextState);
    }

    @Override
    public Optional<ExecutionLifecycleState> getState(String executionId) {
        if (executionId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(executionStates.get(executionId));
    }

    public List<ControlEvent> getEvents(String executionId) {
        List<ControlEvent> events = eventHistory.get(executionId);
        if (events == null) {
            return List.of();
        }
        synchronized (events) {
            return List.copyOf(events);
        }
    }
}
