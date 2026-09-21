package tech.kayys.andalus.execution.core.mobility;

import tech.kayys.andalus.execution.mobility.ExecutionDurableState;
import tech.kayys.andalus.execution.mobility.FencingToken;
import tech.kayys.andalus.execution.mobility.LeaseManager;
import tech.kayys.andalus.execution.mobility.StateTransitionRecord;
import tech.kayys.andalus.execution.mobility.state.ExecutionStateStore;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe, fenced in-memory implementation of {@link ExecutionStateStore}.
 * Rejects stale-generation writes using fencing tokens and optimistic concurrency.
 */
public class InMemoryExecutionStateStore implements ExecutionStateStore {

    private final LeaseManager leaseManager;
    private final Map<String, ExecutionDurableState> states = new ConcurrentHashMap<>();
    private final Map<String, FencingToken> latestTokens = new ConcurrentHashMap<>();
    private final Map<String, List<StateTransitionRecord>> transitionHistory = new ConcurrentHashMap<>();

    public InMemoryExecutionStateStore(LeaseManager leaseManager) {
        this.leaseManager = Objects.requireNonNull(leaseManager, "leaseManager cannot be null");
    }

    @Override
    public Optional<ExecutionDurableState> load(String executionId) {
        if (executionId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(states.get(executionId));
    }

    @Override
    public synchronized void save(ExecutionDurableState state, FencingToken fencingToken) {
        Objects.requireNonNull(state, "state cannot be null");
        Objects.requireNonNull(fencingToken, "fencingToken cannot be null");

        String executionId = state.identity().id();

        // 1. Validate against lease manager if token is active
        if (!leaseManager.validateToken(executionId, fencingToken)) {
            throw new IllegalStateException("Fenced write rejected: fencing token " + fencingToken.generation()
                    + " is invalid or superseded for execution " + executionId);
        }

        // 2. Validate token is not older than any previously accepted token for this execution
        FencingToken previousToken = latestTokens.get(executionId);
        if (previousToken != null && fencingToken.generation() < previousToken.generation()) {
            throw new IllegalStateException("Fenced write rejected: token generation " + fencingToken.generation()
                    + " < highest seen generation " + previousToken.generation());
        }

        // 3. Optimistic sequence check if state already exists
        ExecutionDurableState existing = states.get(executionId);
        long fromSeq = existing != null ? existing.versionSequence() : 0L;
        if (existing != null && state.versionSequence() <= existing.versionSequence()) {
            throw new IllegalStateException("Optimistic concurrency violation: incoming sequence "
                    + state.versionSequence() + " <= current sequence " + existing.versionSequence());
        }

        // 4. Update state and record audit transition
        states.put(executionId, state);
        latestTokens.put(executionId, fencingToken);

        StateTransitionRecord transition = new StateTransitionRecord(
                executionId,
                fromSeq,
                state.versionSequence(),
                fencingToken,
                Instant.now()
        );
        transitionHistory.computeIfAbsent(executionId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(transition);
    }

    @Override
    public long version(String executionId) {
        if (executionId == null) {
            return 0L;
        }
        ExecutionDurableState state = states.get(executionId);
        return state != null ? state.versionSequence() : 0L;
    }

    public List<StateTransitionRecord> getTransitions(String executionId) {
        List<StateTransitionRecord> records = transitionHistory.get(executionId);
        if (records == null) {
            return List.of();
        }
        synchronized (records) {
            return List.copyOf(records);
        }
    }
}
