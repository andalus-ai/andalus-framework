package tech.kayys.wayang.harness.runtime;

import tech.kayys.wayang.harness.api.Harness;
import tech.kayys.wayang.harness.api.HarnessExecution;
import tech.kayys.wayang.harness.api.HarnessRequest;
import tech.kayys.wayang.harness.api.HarnessResult;
import tech.kayys.wayang.harness.context.DefaultHarnessContext;
import tech.kayys.wayang.harness.context.HarnessContext;
import tech.kayys.wayang.harness.environment.CapabilityId;
import tech.kayys.wayang.harness.environment.DefaultHarnessEnvironment;
import tech.kayys.wayang.harness.environment.DefaultHarnessResources;
import tech.kayys.wayang.harness.environment.HarnessCapabilities;
import tech.kayys.wayang.harness.environment.HarnessEnvironment;
import tech.kayys.wayang.harness.environment.HarnessResources;
import tech.kayys.wayang.harness.lifecycle.DefaultHarnessLifecycle;
import tech.kayys.wayang.harness.lifecycle.HarnessExecutionStatus;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Default implementation of {@link Harness} governing agent execution admission, lifecycle, and environment.
 */
public class DefaultHarness implements Harness {

    private final HarnessCapabilities capabilities;
    private final HarnessResources resources;
    private final Executor executor;

    public DefaultHarness(HarnessCapabilities capabilities) {
        this(capabilities, new DefaultHarnessResources(), ForkJoinPool.commonPool());
    }

    public DefaultHarness(HarnessCapabilities capabilities, HarnessResources resources) {
        this(capabilities, resources, ForkJoinPool.commonPool());
    }

    public DefaultHarness(HarnessCapabilities capabilities, HarnessResources resources, Executor executor) {
        this.capabilities = Objects.requireNonNull(capabilities, "capabilities");
        this.resources = Objects.requireNonNull(resources, "resources");
        this.executor = Objects.requireNonNull(executor, "executor");
    }

    @Override
    public HarnessExecution start(HarnessRequest request) {
        Objects.requireNonNull(request, "request");
        String executionId = request.identity().executionId();

        DefaultHarnessLifecycle lifecycle = new DefaultHarnessLifecycle(HarnessExecutionStatus.CREATED);
        DefaultHarnessExecution execution = new DefaultHarnessExecution(executionId, lifecycle);

        // 1. Admission Control: check required capabilities
        for (CapabilityId required : request.requiredCapabilities()) {
            if (!capabilities.has(required)) {
                execution.completeFailure("Admission denied: missing or unauthorized capability: " + required.value());
                return execution;
            }
        }

        lifecycle.transitionTo(HarnessExecutionStatus.ADMITTED);

        // 2. Setup Environment & Context
        HarnessEnvironment environment = new DefaultHarnessEnvironment(
                request.identity(),
                request.session(),
                capabilities,
                resources
        );
        HarnessContext context = new DefaultHarnessContext(
                request.identity(),
                request.session(),
                request.attributes()
        );

        HarnessRuntime runtime = new RuntimeBackedHarnessRuntime(environment, context, lifecycle);

        // 3. Dispatch asynchronous execution
        lifecycle.transitionTo(HarnessExecutionStatus.INITIALIZING);
        executor.execute(() -> {
            try {
                if (execution.status() == HarnessExecutionStatus.CANCELLED) {
                    return;
                }
                lifecycle.transitionTo(HarnessExecutionStatus.RUNNING);

                // Default execution output echoing prompt with execution metadata
                String output = String.format("Agent [%s] executed prompt: %s", request.targetAgentId(), request.prompt());
                execution.completeSuccess(output);
            } catch (Throwable t) {
                execution.completeFailure(t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName());
            }
        });

        return execution;
    }
}
