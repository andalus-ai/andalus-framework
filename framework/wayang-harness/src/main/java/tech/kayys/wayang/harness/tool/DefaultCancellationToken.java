package tech.kayys.wayang.harness.tool;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides the default implementation of the cancellation token contract.
 */


public final class DefaultCancellationToken implements CancellationToken {

    private final AtomicBoolean canceled = new AtomicBoolean(false);

    @Override
    public boolean isCancellationRequested() {
        return canceled.get();
    }

    @Override
    public void cancel() {
        canceled.set(true);
    }
}
