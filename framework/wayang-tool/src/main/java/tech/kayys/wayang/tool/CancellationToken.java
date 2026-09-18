package tech.kayys.wayang.tool;

/**
 * Contract for cooperatively checking and signalling cancellation of an execution.
 */
public interface CancellationToken {

    boolean isCancellationRequested();

    void cancel();

    static CancellationToken create() {
        return new DefaultCancellationToken();
    }

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
}
