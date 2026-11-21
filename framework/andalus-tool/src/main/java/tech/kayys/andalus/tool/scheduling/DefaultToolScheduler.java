package tech.kayys.andalus.tool.scheduling;

import tech.kayys.andalus.tool.CancellationToken;
import tech.kayys.andalus.tool.DefaultToolExecutionHandle;
import tech.kayys.andalus.tool.ToolExecutionContext;
import tech.kayys.andalus.tool.ToolExecutionHandle;
import tech.kayys.andalus.tool.ToolExecutionPolicy;
import tech.kayys.andalus.tool.ToolExecutor;
import tech.kayys.andalus.tool.ToolInvocation;
import tech.kayys.andalus.tool.ToolResult;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

public class DefaultToolScheduler implements ToolScheduler, AutoCloseable {

    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduler;
    private final boolean managesExecutor;

    public DefaultToolScheduler(ExecutorService executorService, ScheduledExecutorService scheduler) {
        this.executorService = executorService != null ? executorService : Executors.newCachedThreadPool();
        this.scheduler = scheduler != null ? scheduler : Executors.newSingleThreadScheduledExecutor();
        this.managesExecutor = (executorService == null);
    }

    public DefaultToolScheduler() {
        this(null, null);
    }

    @Override
    public ToolExecutionHandle submit(
            ToolInvocation invocation,
            ToolExecutionContext context,
            ToolExecutor executor,
            ToolExecutionPolicy policy
    ) {
        CompletableFuture<ToolResult> future = new CompletableFuture<>();
        CancellationToken cancellationToken = CancellationToken.create();
        CancellationToken parentToken = context != null ? context.cancellation() : null;
        CancellationToken.CancellationRegistration parentRegistration = parentToken == null
                ? () -> { }
                : parentToken.onCancel(cancellationToken::cancel);
        ToolExecutionContext executionContext = withCancellation(context, cancellationToken);
        future.whenComplete((result, failure) -> parentRegistration.close());

        Duration timeout = policy != null && policy.timeout() != null ? policy.timeout() : Duration.ofSeconds(30);

        ScheduledFuture<?> timeoutTask = scheduler.schedule(() -> {
            if (!future.isDone()) {
                cancellationToken.cancel();
                future.complete(ToolResult.timeout(invocation.invocationIdentifier(), timeout));
            }
        }, timeout.toMillis(), TimeUnit.MILLISECONDS);

        executorService.submit(() -> {
            try {
                if (cancellationToken.isCancellationRequested()) {
                    future.complete(ToolResult.canceled(invocation.invocationIdentifier()));
                    return;
                }
                ToolResult result = executor.executeBlocking(invocation, executionContext, cancellationToken);
                timeoutTask.cancel(true);
                future.complete(result);
            } catch (CancellationException ce) {
                timeoutTask.cancel(true);
                future.complete(ToolResult.canceled(invocation.invocationIdentifier()));
            } catch (Throwable t) {
                timeoutTask.cancel(true);
                future.complete(ToolResult.failure(invocation.invocationIdentifier(),
                        t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName(),
                        Duration.ZERO, "executor"));
            }
        });

        return new DefaultToolExecutionHandle(invocation.invocationIdentifier(), future, cancellationToken);
    }

    private static ToolExecutionContext withCancellation(
            ToolExecutionContext delegate, CancellationToken cancellation) {
        if (delegate == null) return null;
        return new ToolExecutionContext() {
            @Override public String executionId() { return delegate.executionId(); }
            @Override public CancellationToken cancellation() { return cancellation; }
            @Override public tech.kayys.andalus.tool.ToolLogger logger() { return delegate.logger(); }
            @Override public tech.kayys.andalus.tool.artifact.ArtifactStore artifacts() { return delegate.artifacts(); }
            @Override public Object identity() { return delegate.identity(); }
            @Override public Object resources() { return delegate.resources(); }
            @Override public Map<String, Object> attributes() { return delegate.attributes(); }
            @Override public Optional<Object> getAttribute(String key) { return delegate.getAttribute(key); }
        };
    }

    @Override
    public void close() {
        if (managesExecutor) {
            executorService.shutdown();
            scheduler.shutdown();
        }
    }
}
