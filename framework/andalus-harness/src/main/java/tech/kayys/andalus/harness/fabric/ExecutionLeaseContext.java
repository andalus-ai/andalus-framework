package tech.kayys.andalus.harness.fabric;

import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Cooperative cancellation signal for work protected by an execution lease.
 *
 * <p>Cancellation is advisory: callbacks should interrupt blocking operations
 * where safe, and work should call {@link #throwIfCancellationRequested()}
 * between meaningful units of work. Durable writes must still enforce the
 * execution fencing token at their storage boundary.</p>
 */
public final class ExecutionLeaseContext {
    private final AtomicBoolean cancellationRequested = new AtomicBoolean();
    private final CopyOnWriteArrayList<Runnable> cancellationCallbacks = new CopyOnWriteArrayList<>();

    /** Returns whether this execution has lost lease ownership. */
    public boolean isCancellationRequested() {
        return cancellationRequested.get();
    }

    /** Throws when ownership has been lost or cancellation was otherwise requested. */
    public void throwIfCancellationRequested() {
        if (isCancellationRequested()) {
            throw new CancellationException("Execution lease ownership was lost");
        }
    }

    /**
     * Registers a callback to stop cooperative work. If cancellation already
     * happened, the callback runs immediately. Callback failures are isolated.
     */
    public void onCancellation(Runnable callback) {
        Objects.requireNonNull(callback, "callback cannot be null");
        if (cancellationRequested.get()) {
            runSafely(callback);
            return;
        }
        cancellationCallbacks.add(callback);
        if (cancellationRequested.get() && cancellationCallbacks.remove(callback)) {
            runSafely(callback);
        }
    }

    void cancel() {
        if (!cancellationRequested.compareAndSet(false, true)) {
            return;
        }
        for (Runnable callback : cancellationCallbacks) {
            if (cancellationCallbacks.remove(callback)) {
                runSafely(callback);
            }
        }
    }

    private static void runSafely(Runnable callback) {
        try {
            callback.run();
        } catch (RuntimeException ignored) {
            // Cancellation must continue even if one cooperative callback fails.
        }
    }
}
