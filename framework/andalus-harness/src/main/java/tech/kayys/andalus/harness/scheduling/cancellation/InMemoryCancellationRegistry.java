package tech.kayys.andalus.harness.scheduling.cancellation;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of {@link CancellationRegistry}.
 */
public class InMemoryCancellationRegistry implements CancellationRegistry {

    private final Map<ExecutionId, Runnable> signals = new ConcurrentHashMap<>();

    @Override
    public void register(ExecutionId executionId, Runnable cancelSignal) {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(cancelSignal, "cancelSignal");
        signals.put(executionId, cancelSignal);
    }

    @Override
    public void unregister(ExecutionId executionId) {
        if (executionId != null) {
            signals.remove(executionId);
        }
    }

    @Override
    public void signal(ExecutionId executionId) {
        if (executionId != null) {
            Runnable signal = signals.get(executionId);
            if (signal != null) {
                signal.run();
            }
        }
    }
}
