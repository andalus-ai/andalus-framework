package tech.kayys.andalus.execution.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.communication.api.AgentRef;
import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.api.AgentResponse;
import tech.kayys.andalus.communication.capability.CapabilityId;
import tech.kayys.andalus.communication.message.MessagePayload;
import tech.kayys.andalus.execution.AgentInvocation;
import tech.kayys.andalus.execution.AgentInvocationChain;
import tech.kayys.andalus.execution.AgentInvocationFactory;
import tech.kayys.andalus.execution.AgentInvocationHandler;
import tech.kayys.andalus.execution.AgentInvocationInterceptor;
import tech.kayys.andalus.execution.AgentResult;
import tech.kayys.andalus.execution.ExecutionLifecycle;
import tech.kayys.andalus.execution.pipeline.ExecutionPipeline;
import tech.kayys.andalus.security.identity.Principal;
import tech.kayys.andalus.security.propagation.SecurityContextSnapshot;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionPipelineTest {

    private AgentInvocationFactory factory;
    private InterceptorPipeline interceptorPipeline;
    private SecurityContextSnapshot security;

    @BeforeEach
    void setUp() {
        factory = new DefaultAgentInvocationFactory(
                new DefaultExecutionIdGenerator(),
                new DefaultCancellationTokenFactory()
        );
        interceptorPipeline = new InterceptorPipeline();
        security = new SecurityContextSnapshot(
                Principal.anonymous(), TenantContext.empty(), Optional.empty(), Map.of()
        );
    }

    @Test
    void executesTerminalHandlerWhenNoInterceptors() throws Exception {
        AgentInvocationHandler terminal = invocation ->
                CompletableFuture.completedFuture(
                        AgentResult.of(AgentResponse.success(MessagePayload.text("done")))
                );

        ExecutionPipeline pipeline = new DefaultExecutionPipeline(interceptorPipeline, terminal);

        AgentRequest request = AgentRequest.of(
                AgentRef.local("agent-1", "Test", "rt-1"),
                CapabilityId.of("cap.test"),
                MessagePayload.text("hello")
        );
        AgentInvocation invocation = factory.root(request, security);
        authorize(invocation);

        AgentResult result = pipeline.execute(invocation).toCompletableFuture().get();

        assertNotNull(result);
        assertEquals(ExecutionLifecycle.COMPLETED, invocation.context().lifecycle());
    }

    @Test
    void interceptorOrderIsRespected() throws Exception {
        AtomicInteger callOrder = new AtomicInteger(0);

        AgentInvocationInterceptor first = new AgentInvocationInterceptor() {
            @Override public String name() { return "first"; }
            @Override public int order() { return 10; }
            @Override
            public CompletionStage<AgentResult> intercept(AgentInvocation inv, AgentInvocationChain chain) {
                assertEquals(0, callOrder.getAndIncrement(), "first should be called first");
                return chain.proceed(inv);
            }
        };

        AgentInvocationInterceptor second = new AgentInvocationInterceptor() {
            @Override public String name() { return "second"; }
            @Override public int order() { return 20; }
            @Override
            public CompletionStage<AgentResult> intercept(AgentInvocation inv, AgentInvocationChain chain) {
                assertEquals(1, callOrder.getAndIncrement(), "second should be called second");
                return chain.proceed(inv);
            }
        };

        // register out of order
        interceptorPipeline.register(second);
        interceptorPipeline.register(first);

        AgentInvocationHandler terminal = invocation ->
                CompletableFuture.completedFuture(
                        AgentResult.of(AgentResponse.success(MessagePayload.text("ok")))
                );

        ExecutionPipeline pipeline = new DefaultExecutionPipeline(interceptorPipeline, terminal);

        AgentRequest request = AgentRequest.of(
                AgentRef.local("agent-1", "Test", "rt-1"),
                CapabilityId.of("cap.test"),
                MessagePayload.text("hello")
        );
        AgentInvocation invocation = factory.root(request, security);
        authorize(invocation);
        pipeline.execute(invocation).toCompletableFuture().get();

        assertEquals(2, callOrder.get());
    }

    @Test
    void lifecycleTransitionsToFailedOnError() throws Exception {
        AgentInvocationHandler terminal = invocation ->
                CompletableFuture.failedFuture(new RuntimeException("agent error"));

        ExecutionPipeline pipeline = new DefaultExecutionPipeline(interceptorPipeline, terminal);

        AgentRequest request = AgentRequest.of(
                AgentRef.local("agent-1", "Test", "rt-1"),
                CapabilityId.of("cap.test"),
                MessagePayload.text("hello")
        );
        AgentInvocation invocation = factory.root(request, security);
        authorize(invocation);

        assertThrows(Exception.class,
                () -> pipeline.execute(invocation).toCompletableFuture().get()
        );
        assertEquals(ExecutionLifecycle.FAILED, invocation.context().lifecycle());
    }

    @Test
    void rejectsInvocationThatHasNotBeenAuthorized() {
        AtomicInteger terminalCalls = new AtomicInteger();
        AgentInvocationHandler terminal = invocation -> {
            terminalCalls.incrementAndGet();
            return CompletableFuture.completedFuture(
                    AgentResult.of(AgentResponse.success(MessagePayload.text("should not run")))
            );
        };
        ExecutionPipeline pipeline = new DefaultExecutionPipeline(interceptorPipeline, terminal);

        AgentRequest request = AgentRequest.of(
                AgentRef.local("agent-1", "Test", "rt-1"),
                CapabilityId.of("cap.test"),
                MessagePayload.text("hello")
        );
        AgentInvocation invocation = factory.root(request, security);

        assertThrows(IllegalStateException.class, () -> pipeline.execute(invocation));
        assertEquals(0, terminalCalls.get());
        assertEquals(ExecutionLifecycle.CREATED, invocation.context().lifecycle());
    }

    private void authorize(AgentInvocation invocation) {
        invocation.context().transition(ExecutionLifecycle.AUTHORIZING);
        invocation.context().transition(ExecutionLifecycle.AUTHORIZED);
    }
}\n