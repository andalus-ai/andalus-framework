package tech.kayys.wayang.harness.tool;

import java.util.concurrent.CancellationException;

public interface CancellationToken {

    boolean isCancellationRequested();

    default void throwIfCancellationRequested() {
        if (isCancellationRequested()) {
            throw new CancellationException("Operation was canceled");
        }
    }

    void cancel();

    static CancellationToken none() {
        return new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @Override
            public void cancel() {
                // no-op
            }
        };
    }

    static CancellationToken create() {
        return new DefaultCancellationToken();
    }
}
