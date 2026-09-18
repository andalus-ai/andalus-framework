package tech.kayys.wayang.tool;

import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultCancellationToken implements CancellationToken {

    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    @Override
    public boolean isCancellationRequested() {
        return cancelled.get();
    }

    @Override
    public void cancel() {
        cancelled.set(true);
    }
}
