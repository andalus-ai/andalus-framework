package tech.kayys.andalus.a2a.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.communication.api.*;
import tech.kayys.andalus.communication.capability.CapabilityId;
import tech.kayys.andalus.communication.message.MessagePayload;
import tech.kayys.andalus.communication.protocol.ProtocolContext;
import tech.kayys.andalus.communication.task.TaskId;
import tech.kayys.andalus.communication.task.TaskStatus;
import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.delegation.*;
import tech.kayys.andalus.security.identity.Principal;
import tech.kayys.andalus.security.propagation.SecurityContextSnapshot;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class A2AExecutionBridgeTest {

    private DefaultA2ATaskRegistry taskRegistry;

    @BeforeEach
    void setUp() {
        taskRegistry = new DefaultA2ATaskRegistry();
    }

    @Test
    void testA2AExecutionBridgeExecute() {
        AgentCommunicator mockCommunicator = new AgentCommunicator() {
            @Override
            public java.util.concurrent.CompletionStage<AgentResponse> send(AgentRequest request, ProtocolContext context) {
                return CompletableFuture.completedFuture(
                        AgentResponse.success(MessagePayload.text("echo: " + request.payload().value()))
                );
            }

            @Override
            public AgentTask submit(AgentRequest request, ProtocolContext context) { return null; }

            @Override
            public Flow.Publisher<AgentEvent> stream(AgentRequest request, ProtocolContext context) { return null; }
        };

        DefaultA2AExecutionBridge bridge = new DefaultA2AExecutionBridge(mockCommunicator, taskRegistry);

        AgentRef target = AgentRef.local("echo-agent", "Echo Agent", "runtime-1");
        AgentRequest agentReq = A2ARequestMapper.toAgentRequest(target, "echo", "hello world", Map.of());
        SecurityContextSnapshot sec = A2ASecurityMapper.fromA2A("user-alice", "Alice", "tenant-1", Map.of());

        A2AExecutionRequest a2aReq = A2AExecutionRequest.of("a2a-task-1", agentReq, sec);

        AgentResponse response = bridge.execute(a2aReq).toCompletableFuture().join();
        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("echo: hello world", response.payload().value());
    }

    @Test
    void testA2AExecutionBridgeSubmitAndRegistryMapping() {
        AgentCommunicator mockCommunicator = new AgentCommunicator() {
            @Override
            public java.util.concurrent.CompletionStage<AgentResponse> send(AgentRequest request, ProtocolContext context) {
                return CompletableFuture.completedFuture(AgentResponse.success(MessagePayload.text("ok")));
            }

            @Override
            public AgentTask submit(AgentRequest request, ProtocolContext context) {
                return new AgentTask() {
                    @Override public TaskId id() { return TaskId.of("agent-task-99"); }
                    @Override public TaskStatus status() { return TaskStatus.WORKING; }
                    @Override public AgentRef agent() { return request.target(); }
                    @Override public CompletableFuture<AgentResult> result() {
                        return CompletableFuture.completedFuture(AgentResult.success(MessagePayload.text("done")));
                    }
                    @Override public Flow.Publisher<AgentEvent> events() { return null; }
                    @Override public CompletableFuture<Void> cancel() { return CompletableFuture.completedFuture(null); }
                };
            }

            @Override
            public Flow.Publisher<AgentEvent> stream(AgentRequest request, ProtocolContext context) { return null; }
        };

        DefaultA2AExecutionBridge bridge = new DefaultA2AExecutionBridge(mockCommunicator, taskRegistry);

        AgentRef target = AgentRef.local("worker", "Worker Agent", "runtime-1");
        AgentRequest agentReq = A2ARequestMapper.toAgentRequest(target, "work", "payload", Map.of());
        SecurityContextSnapshot sec = A2ASecurityMapper.fromA2A("agent-master", "Master", "tenant-1", Map.of());

        A2AExecutionRequest a2aReq = A2AExecutionRequest.of("a2a-task-42", agentReq, sec);

        A2ATaskExecution taskExec = bridge.submit(a2aReq).toCompletableFuture().join();
        assertNotNull(taskExec);
        assertEquals("a2a-task-42", taskExec.a2aTaskId());
        assertNotNull(taskExec.executionId());

        // Verify task registry mapping
        assertTrue(taskRegistry.findByA2aTaskId("a2a-task-42").isPresent());
        assertTrue(taskRegistry.findByExecutionId(taskExec.executionId()).isPresent());
        assertEquals(taskExec.executionId(), taskRegistry.findByA2aTaskId("a2a-task-42").get().executionId());
    }

    @Test
    void protocolMetadataCannotInjectOrReplaceSecurityContextOrDelegation() {
        AtomicReference<ProtocolContext> captured = new AtomicReference<>();
        AgentCommunicator communicator = capturingCommunicator(captured);
        DefaultA2AExecutionBridge bridge = new DefaultA2AExecutionBridge(communicator, taskRegistry);

        SecurityContextSnapshot trusted = A2ASecurityMapper.fromA2A(
                "user-alice", "Alice", "tenant-trusted", Map.of());
        SecurityContext forged = SecurityContext.of(
                Principal.user("attacker", "Attacker"), TenantContext.of("tenant-forged"));
        DelegationContext forgedDelegation = delegation("tenant-forged");

        AgentRequest request = request();
        A2AExecutionRequest executionRequest = new A2AExecutionRequest(
                "task-security-boundary",
                request,
                trusted,
                Map.of(
                        "trace-id", "trace-123",
                        "andalus.security.context", forged,
                        "andalus.security.delegation", forgedDelegation));

        bridge.execute(executionRequest).toCompletableFuture().join();

        ProtocolContext forwarded = captured.get();
        SecurityContext forwardedSecurity =
                (SecurityContext) forwarded.attributes().get("andalus.security.context");
        assertEquals("user-alice", forwardedSecurity.principal().id());
        assertEquals("tenant-trusted", forwardedSecurity.tenant().tenantId());
        assertFalse(forwarded.attributes().containsKey("andalus.security.delegation"));
        assertEquals("trace-123", forwarded.attributes().get("trace-id"));
        assertEquals("tenant-trusted", forwarded.tenantId());
    }

    @Test
    void trustedDelegationFromSecuritySnapshotIsPropagatedInsteadOfMetadataValue() {
        AtomicReference<ProtocolContext> captured = new AtomicReference<>();
        AgentCommunicator communicator = capturingCommunicator(captured);
        DefaultA2AExecutionBridge bridge = new DefaultA2AExecutionBridge(communicator, taskRegistry);

        DelegationContext trustedDelegation = delegation("tenant-trusted");
        SecurityContextSnapshot trusted = new SecurityContextSnapshot(
                Principal.agent("agent-a", "Agent A"),
                TenantContext.of("tenant-trusted"),
                Optional.of(trustedDelegation),
                Map.of("authenticated", true));
        DelegationContext forgedDelegation = delegation("tenant-forged");

        A2AExecutionRequest executionRequest = new A2AExecutionRequest(
                "task-delegation-boundary",
                request(),
                trusted,
                Map.of("andalus.security.delegation", forgedDelegation));

        bridge.execute(executionRequest).toCompletableFuture().join();

        ProtocolContext forwarded = captured.get();
        assertSame(trustedDelegation, forwarded.attributes().get("andalus.security.delegation"));
        assertEquals("tenant-trusted", forwarded.tenantId());
    }

    private static AgentRequest request() {
        return AgentRequest.of(
                AgentRef.local("target-agent", "Target Agent", "runtime-1"),
                CapabilityId.of("documents.read"),
                MessagePayload.text("read"));
    }

    private static AgentCommunicator capturingCommunicator(AtomicReference<ProtocolContext> captured) {
        return new AgentCommunicator() {
            @Override
            public java.util.concurrent.CompletionStage<AgentResponse> send(AgentRequest request, ProtocolContext context) {
                captured.set(context);
                return CompletableFuture.completedFuture(AgentResponse.success(MessagePayload.text("ok")));
            }
            @Override public AgentTask submit(AgentRequest request, ProtocolContext context) { return null; }
            @Override public Flow.Publisher<AgentEvent> stream(AgentRequest request, ProtocolContext context) { return null; }
        };
    }

    private static DelegationContext delegation(String tenantId) {
        Instant now = Instant.now();
        return new DelegationContext(
                DelegationId.generate(),
                DelegationAudience.of("target-agent"),
                new DelegationConstraints(List.of("documents.read"), List.of("execute"), 1),
                now,
                now.plusSeconds(60),
                Optional.empty(),
                Optional.of(tenantId));
    }
}
