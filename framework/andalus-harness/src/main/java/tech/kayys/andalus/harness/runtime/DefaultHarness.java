package tech.kayys.andalus.harness.runtime;

import tech.kayys.andalus.harness.api.Harness;
import tech.kayys.andalus.harness.api.HarnessExecution;
import tech.kayys.andalus.harness.api.HarnessRequest;
import tech.kayys.andalus.harness.api.HarnessResult;
import tech.kayys.andalus.harness.context.DefaultHarnessContext;
import tech.kayys.andalus.harness.context.HarnessContext;
import tech.kayys.andalus.harness.environment.CapabilityId;
import tech.kayys.andalus.harness.environment.DefaultHarnessEnvironment;
import tech.kayys.andalus.harness.environment.DefaultHarnessResources;
import tech.kayys.andalus.harness.environment.HarnessCapabilities;
import tech.kayys.andalus.harness.environment.HarnessEnvironment;
import tech.kayys.andalus.harness.environment.HarnessResources;
import tech.kayys.andalus.harness.lifecycle.DefaultHarnessLifecycle;
import tech.kayys.andalus.harness.lifecycle.HarnessExecutionStatus;
import tech.kayys.andalus.harness.resource.DefaultResourceScope;
import tech.kayys.andalus.harness.resource.ResourceDecision;
import tech.kayys.andalus.harness.resource.ResourceRequest;
import tech.kayys.andalus.harness.resource.ResourceScope;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Default implementation of {@link Harness} governing agent execution admission, lifecycle, environment, and resources.
 */
public class DefaultHarness implements Harness {

    private final HarnessCapabilities capabilities;
    private final HarnessResources resources;
    private final ResourceScope resourceScope;
    private final Executor executor;
    private final tech.kayys.andalus.harness.context.ContextAssembler contextAssembler;

    public DefaultHarness(HarnessCapabilities capabilities) {
        this(capabilities, new DefaultHarnessResources(), DefaultResourceScope.allowAll(), ForkJoinPool.commonPool(), null);
    }

    public DefaultHarness(HarnessCapabilities capabilities, HarnessResources resources) {
        this(capabilities, resources, DefaultResourceScope.allowAll(), ForkJoinPool.commonPool(), null);
    }

    public DefaultHarness(HarnessCapabilities capabilities, HarnessResources resources, Executor executor) {
        this(capabilities, resources, DefaultResourceScope.allowAll(), executor, null);
    }

    public DefaultHarness(HarnessCapabilities capabilities, HarnessResources resources, ResourceScope resourceScope, Executor executor) {
        this(capabilities, resources, resourceScope, executor, null);
    }

    public DefaultHarness(
            HarnessCapabilities capabilities,
            HarnessResources resources,
            ResourceScope resourceScope,
            Executor executor,
            tech.kayys.andalus.harness.context.ContextAssembler contextAssembler) {
        this.capabilities = Objects.requireNonNull(capabilities, "capabilities");
        this.resources = Objects.requireNonNull(resources, "resources");
        this.resourceScope = Objects.requireNonNull(resourceScope, "resourceScope");
        this.executor = Objects.requireNonNull(executor, "executor");
        this.contextAssembler = contextAssembler;
    }

    public tech.kayys.andalus.harness.context.ContextAssembler contextAssembler() {
        return contextAssembler;
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

        // 2. Admission Control: check required resources (Scope & Quota)
        for (ResourceRequest resourceReq : request.requiredResources()) {
            ResourceDecision decision = resourceScope.evaluate(resourceReq);
            if (!decision.isAllowed()) {
                execution.completeFailure("Admission denied: resource rejected: " + decision.reason());
                return execution;
            }
            if (!resources.quota().canAllocate(resourceReq)) {
                execution.completeFailure("Admission denied: resource quota exceeded for type " + resourceReq.type());
                return execution;
            }
        }

        lifecycle.transitionTo(HarnessExecutionStatus.ADMITTED);

        // 3. Setup Environment & Context
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

        // 4. Dispatch asynchronous execution
        lifecycle.transitionTo(HarnessExecutionStatus.INITIALIZING);
        executor.execute(() -> {
            try {
                if (execution.status() == HarnessExecutionStatus.CANCELLED) {
                    return;
                }
                lifecycle.transitionTo(HarnessExecutionStatus.RUNNING);

                // Context assembly if assembler is provided
                tech.kayys.andalus.harness.context.AssembledContext assembledContext = null;
                if (contextAssembler != null) {
                    tech.kayys.andalus.harness.context.ContextRequest ctxReq =
                            tech.kayys.andalus.harness.context.ContextRequest.of(request.prompt(), 8192);
                    assembledContext = contextAssembler.assemble(ctxReq);
                }

                // Execution output echoing prompt with execution metadata and context diagnostics if assembled
                String output;
                if (assembledContext != null) {
                    output = String.format("Agent [%s] executed prompt: %s [contextItems=%d, estimatedTokens=%d]",
                            request.targetAgentId(), request.prompt(), assembledContext.items().size(), assembledContext.estimatedTokens());
                } else {
                    output = String.format("Agent [%s] executed prompt: %s", request.targetAgentId(), request.prompt());
                }
                execution.completeSuccess(output);
            } catch (Throwable t) {
                execution.completeFailure(t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName());
            }
        });

        return execution;
    }
}
