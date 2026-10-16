package tech.kayys.andalus.a2a.durable;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2a.execution.A2AExecutionBridge;
import tech.kayys.andalus.a2a.execution.A2AExecutionRequest;
import tech.kayys.andalus.a2a.execution.A2ARequestMapper;
import tech.kayys.andalus.a2a.execution.A2ASecurityMapper;
import tech.kayys.andalus.a2a.execution.A2ATaskExecution;
import tech.kayys.andalus.a2a.execution.DefaultA2AExecutionBridge;
import tech.kayys.andalus.a2a.execution.DefaultA2ATaskRegistry;
import tech.kayys.andalus.communication.api.AgentCommunicator;
import tech.kayys.andalus.communication.api.AgentEvent;
import tech.kayys.andalus.communication.api.AgentRef;
import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.api.AgentResponse;
import tech.kayys.andalus.communication.api.AgentTask;
import tech.kayys.andalus.communication.message.MessagePayload;
import tech.kayys.andalus.communication.protocol.ProtocolContext;
import tech.kayys.andalus.security.propagation.SecurityContextSnapshot;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for A2A correlation, intent, and execution bridge (6.16).
 */
class A2AExecutionCorrelationTest {

    @Test
    void a2aExecutionRequest_supportsCallerExecutionId() {
        AgentRequest req = A2ARequestMapper.toAgentRequest(
                AgentRef.local("test-agent", "Test Agent", "runtime-1"), "echo", "hello", Map.of());
        SecurityContextSnapshot sec = A2ASecurityMapper.fromA2A("user1", "User One", "tenant-1", Map.of());

        // Without callerExecutionId
        A2AExecutionRequest legacyReq = A2AExecutionRequest.of("task-1", req, sec);
        assertFalse(legacyReq.isDurable());
        assertTrue(legacyReq.callerExecutionId().isEmpty());

        // With callerExecutionId
        A2AExecutionRequest durableReq = A2AExecutionRequest.of("task-2", req, sec, "exec-durable-123");
        assertTrue(durableReq.isDurable());
        assertEquals("exec-durable-123", durableReq.callerExecutionId().get());
    }

    @Test
    void defaultA2AExecutionBridge_reusesCallerExecutionId() throws Exception {
        AgentCommunicator communicator = new AgentCommunicator() {
            @Override
            public CompletionStage<AgentResponse> send(AgentRequest request, ProtocolContext context) {
                return CompletableFuture.completedFuture(
                        AgentResponse.success(MessagePayload.text("ok")));
            }

            @Override
            public AgentTask submit(AgentRequest request, ProtocolContext context) {
                return null;
            }

            @Override
            public Flow.Publisher<AgentEvent> stream(AgentRequest request, ProtocolContext context) {
                return null;
            }
        };
        A2AExecutionBridge bridge = new DefaultA2AExecutionBridge(communicator, new DefaultA2ATaskRegistry());

        AgentRequest req = A2ARequestMapper.toAgentRequest(
                AgentRef.local("test-agent", "Test Agent", "runtime-1"), "echo", "hello", Map.of());
        SecurityContextSnapshot sec = A2ASecurityMapper.fromA2A("user2", "User Two", "tenant-2", Map.of());

        // Submit durable request with callerExecutionId
        A2AExecutionRequest durableReq = A2AExecutionRequest.of("task-a2a-99", req, sec, "exec-parent-456");
        A2ATaskExecution taskExec = bridge.submit(durableReq).toCompletableFuture().get();

        assertNotNull(taskExec);
        assertEquals("task-a2a-99", taskExec.a2aTaskId());
        assertEquals("exec-parent-456", taskExec.executionId(),
                "Durable request must reuse the callerExecutionId (6.16.1.7)");
    }

    @Test
    void a2aExecutionCorrelation_tracksThreeIdentities() {
        A2AExecutionCorrelation correlation = A2AExecutionCorrelation.of("exec-local-1", "task-protocol-2");
        assertEquals("exec-local-1", correlation.callerExecutionId());
        assertEquals("task-protocol-2", correlation.taskId());
        assertTrue(correlation.remoteExecutionId().isEmpty());

        A2AExecutionCorrelation bound = correlation.withRemoteExecution("exec-remote-3");
        assertEquals("exec-remote-3", bound.remoteExecutionId().get());
    }

    @Test
    void a2aInvocationIntent_keyedAndUnkeyed() {
        A2AInvocationIntent intent = A2AInvocationIntent.keyed(
                "exec-1", "op-1", "task-1", "http://remote:8080", "idemp-key-1"
        );
        assertEquals("exec-1", intent.executionId());
        assertEquals("op-1", intent.operationId());
        assertEquals("task-1", intent.taskId());
        assertEquals("http://remote:8080", intent.remoteEndpoint());
        assertEquals("idemp-key-1", intent.idempotencyKey().get());

        A2AInvocationIntent unkeyed = A2AInvocationIntent.withoutKey(
                "exec-2", "op-2", "task-2", "http://remote:8080"
        );
        assertTrue(unkeyed.idempotencyKey().isEmpty());
    }
}
