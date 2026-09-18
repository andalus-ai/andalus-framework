package tech.kayys.wayang.tool;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultToolExecutionHandle implements ToolExecutionHandle {

    private final ToolInvocationId id;
    private final CompletableFuture<ToolResult> future;
    private final CancellationToken cancellationToken;
    private final AtomicReference<ToolExecutionStatus> status;

    public DefaultToolExecutionHandle(
            ToolInvocationId id,
            CompletableFuture<ToolResult> future,
            CancellationToken cancellationToken
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.future = Objects.requireNonNull(future, "future cannot be null");
        this.cancellationToken = cancellationToken != null ? cancellationToken : CancellationToken.none();
        this.status = new AtomicReference<>(ToolExecutionStatus.RUNNING);

        future.whenComplete((res, err) -> {
            if (err != null) {
                status.set(ToolExecutionStatus.FAILED);
            } else if (res != null) {
                switch (res.status()) {
                    case SUCCESS -> status.set(ToolExecutionStatus.COMPLETED);
                    case CANCELED -> status.set(ToolExecutionStatus.CANCELED);
                    case TIMEOUT -> status.set(ToolExecutionStatus.TIMED_OUT);
                    default -> status.set(ToolExecutionStatus.FAILED);
                }
            }
        });
    }

    @Override
    public ToolInvocationId id() {
        return id;
    }

    @Override
    public ToolExecutionStatus status() {
        return status.get();
    }

    @Override
    public CompletableFuture<ToolResult> result() {
        return future;
    }

    @Override
    public void cancel() {
        cancellationToken.cancel();
        status.set(ToolExecutionStatus.CANCELED);
        future.complete(ToolResult.canceled(id));
    }
}
