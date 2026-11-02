package tech.kayys.andalus.execution.core.attempt;

import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.attempt.AttemptLineage;
import tech.kayys.andalus.execution.attempt.ExecutionAttempt;
import tech.kayys.andalus.execution.attempt.ExecutionAttemptStore;
import tech.kayys.andalus.execution.mobility.FencingToken;
import tech.kayys.andalus.execution.mobility.LeaseManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Process-local, fenced test/development implementation of {@link ExecutionAttemptStore}. */
public class InMemoryExecutionAttemptStore implements ExecutionAttemptStore {

    private final LeaseManager leaseManager;
    private final Map<AttemptId, ExecutionAttempt> attempts = new HashMap<>();
    private final Map<AttemptId, AttemptLineage> lineages = new HashMap<>();

    public InMemoryExecutionAttemptStore(LeaseManager leaseManager) {
        this.leaseManager = Objects.requireNonNull(leaseManager, "leaseManager cannot be null");
    }

    @Override
    public synchronized void save(ExecutionAttempt attempt, AttemptLineage lineage, FencingToken fencingToken) {
        Objects.requireNonNull(attempt, "attempt cannot be null");
        Objects.requireNonNull(lineage, "lineage cannot be null");
        Objects.requireNonNull(fencingToken, "fencingToken cannot be null");
        if (!leaseManager.validateToken(attempt.executionId(), fencingToken)) {
            throw new IllegalStateException("Fenced attempt write rejected for execution " + attempt.executionId());
        }
        if (!attempt.executionId().equals(lineage.executionId())
                || !attempt.attemptId().equals(lineage.attemptId())) {
            throw new IllegalArgumentException("attempt and lineage identities must match");
        }
        if (attempts.containsKey(attempt.attemptId())) {
            throw new IllegalStateException("Attempt already exists: " + attempt.attemptId().value());
        }
        Optional<ExecutionAttempt> latest = getLatest(attempt.executionId());
        int expectedNumber = latest.map(value -> value.number() + 1).orElse(1);
        if (attempt.number() != expectedNumber) {
            throw new IllegalStateException("Attempt number must be sequential; expected " + expectedNumber);
        }
        attempts.put(attempt.attemptId(), attempt);
        lineages.put(attempt.attemptId(), lineage);
    }

    @Override
    public synchronized Optional<ExecutionAttempt> get(AttemptId attemptId) {
        return Optional.ofNullable(attempts.get(attemptId));
    }

    @Override
    public synchronized Optional<ExecutionAttempt> getLatest(String executionId) {
        return attempts.values().stream()
                .filter(attempt -> attempt.executionId().equals(executionId))
                .max(Comparator.comparingInt(ExecutionAttempt::number));
    }

    @Override
    public synchronized Optional<AttemptLineage> getLineage(AttemptId attemptId) {
        return Optional.ofNullable(lineages.get(attemptId));
    }

    @Override
    public synchronized List<ExecutionAttempt> list(String executionId) {
        List<ExecutionAttempt> result = new ArrayList<>();
        attempts.values().stream()
                .filter(attempt -> attempt.executionId().equals(executionId))
                .sorted(Comparator.comparingInt(ExecutionAttempt::number))
                .forEach(result::add);
        return List.copyOf(result);
    }
}
