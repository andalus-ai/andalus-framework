package tech.kayys.wayang.harness.tool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

class ToolSchedulerTest {

    private DefaultToolScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new DefaultToolScheduler();
    }

    @AfterEach
    void tearDown() {
        scheduler.close();
    }

    @Test
    void shouldExecuteToolAsynchronously() throws ExecutionException, InterruptedException {
        ToolInvocation invocation = ToolInvocation.of(ToolId.of("calc"), ToolArguments.empty());
        ToolExecutionContext context = DefaultToolExecutionContext.of(null, null, null);
        ToolExecutor executor = (inv, ctx) -> ToolResult.success(inv.id(), "42", Duration.ofMillis(10), "calc");

        ToolExecutionHandle handle = scheduler.submit(invocation, context, executor);

        ToolResult result = handle.result().get();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.output().payload()).isEqualTo("42");
        assertThat(handle.status()).isEqualTo(ToolExecutionStatus.COMPLETED);
    }

    @Test
    void shouldHandleTimeout() throws ExecutionException, InterruptedException {
        ToolInvocation invocation = ToolInvocation.of(ToolId.of("slow"), ToolArguments.empty());
        ToolExecutionContext context = DefaultToolExecutionContext.of(null, null, null);
        ToolExecutor slowExecutor = (inv, ctx) -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
            return ToolResult.success(inv.id(), "done", Duration.ofMillis(500), "slow");
        };

        ToolExecutionPolicy tightPolicy = ToolExecutionPolicy.withTimeout(Duration.ofMillis(50));
        ToolExecutionHandle handle = scheduler.submit(invocation, context, slowExecutor, tightPolicy);

        ToolResult result = handle.result().get();
        assertThat(result.status()).isEqualTo(ToolResultStatus.TIMEOUT);
        assertThat(handle.status()).isEqualTo(ToolExecutionStatus.TIMED_OUT);
    }

    @Test
    void shouldHandleManualCancellation() throws ExecutionException, InterruptedException {
        ToolInvocation invocation = ToolInvocation.of(ToolId.of("canceller"), ToolArguments.empty());
        ToolExecutionContext context = DefaultToolExecutionContext.of(null, null, null);
        ToolExecutor slowExecutor = (inv, ctx) -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
            return ToolResult.success(inv.id(), "done", Duration.ofMillis(1000), "slow");
        };

        ToolExecutionHandle handle = scheduler.submit(invocation, context, slowExecutor);
        handle.cancel();

        ToolResult result = handle.result().get();
        assertThat(result.status()).isEqualTo(ToolResultStatus.CANCELED);
        assertThat(handle.status()).isEqualTo(ToolExecutionStatus.CANCELED);
    }
}
