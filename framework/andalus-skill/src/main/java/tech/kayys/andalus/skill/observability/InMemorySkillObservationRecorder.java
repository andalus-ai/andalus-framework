package tech.kayys.andalus.skill.observability;

import tech.kayys.andalus.skill.spi.SkillResult;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe in-memory recorder capturing execution events for test,
 * dev, and local analytics usage.
 */
public final class InMemorySkillObservationRecorder implements SkillObservationRecorder {

    public record ExecutionRecord(
            SkillObservationContext context,
            SkillResult result,
            Throwable error,
            Instant completedAt,
            Duration duration
    ) {
        public boolean isSuccess() {
            return error == null && result != null && result.isSuccess();
        }
    }

    private final List<SkillObservationContext> startedEvents = new CopyOnWriteArrayList<>();
    private final List<ExecutionRecord> completedRecords = new CopyOnWriteArrayList<>();
    private final Map<String, Instant> activeStarts = new ConcurrentHashMap<>();

    @Override
    public void executionStarted(SkillObservationContext context) {
        startedEvents.add(context);
        activeStarts.put(context.invocationId(), context.startedAt());
    }

    @Override
    public void executionCompleted(SkillObservationContext context, SkillResult result) {
        Instant completedAt = Instant.now();
        Instant startedAt = activeStarts.getOrDefault(context.invocationId(), context.startedAt());
        Duration duration = Duration.between(startedAt, completedAt);

        completedRecords.add(new ExecutionRecord(context, result, null, completedAt, duration));
    }

    @Override
    public void executionFailed(SkillObservationContext context, Throwable error) {
        Instant completedAt = Instant.now();
        Instant startedAt = activeStarts.getOrDefault(context.invocationId(), context.startedAt());
        Duration duration = Duration.between(startedAt, completedAt);

        completedRecords.add(new ExecutionRecord(context, null, error, completedAt, duration));
    }

    public List<SkillObservationContext> startedEvents() {
        return List.copyOf(startedEvents);
    }

    public List<ExecutionRecord> completedRecords() {
        return List.copyOf(completedRecords);
    }

    public void clear() {
        startedEvents.clear();
        completedRecords.clear();
        activeStarts.clear();
    }
}
