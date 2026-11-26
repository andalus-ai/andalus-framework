package tech.kayys.andalus.tool;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultCancellationToken implements CancellationToken {
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final CopyOnWriteArrayList<Runnable> listeners = new CopyOnWriteArrayList<>();

    @Override public boolean isCancellationRequested() { return cancelled.get(); }

    @Override public void cancel() {
        if (!cancelled.compareAndSet(false, true)) return;
        for (Runnable listener : listeners) {
            try { listener.run(); } catch (RuntimeException ignored) {
                // One observer must not prevent cancellation of other resources.
            }
        }
        listeners.clear();
    }

    @Override public CancellationRegistration onCancel(Runnable action) {
        java.util.Objects.requireNonNull(action, "action");
        AtomicBoolean invoked = new AtomicBoolean(false);
        Runnable once = () -> { if (invoked.compareAndSet(false, true)) action.run(); };
        if (cancelled.get()) {
            once.run();
            return () -> { };
        }
        listeners.add(once);
        if (cancelled.get()) {
            listeners.remove(once);
            once.run();
        }
        return () -> listeners.remove(once);
    }
}
