package tech.kayys.andalus.harness.coordination;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GamelanClawBridge / AgentExecutionBinding (6.17).
 *
 * Verifies:
 * - Node dispatch creates a unique ExecutionId (never equal to WorkflowRunId/NodeId)
 * - Idempotent binding on duplicate dispatch
 * - Tenant scoping
 * - Reconciliation returns existing binding
 * - findByExecutionId works in both directions
 */
class GamelanClawBridgeTest {

    private DefaultGamelanClawBridge bridge;

    @BeforeEach
    void setUp() {
        bridge = new DefaultGamelanClawBridge();
    }

    @Test
    void bindNodeExecution_createsBinding() {
        AgentExecutionBinding binding = bridge.bindNodeExecution(
                "tenant-1", "run-100", "research-agent", "res-001");

        assertNotNull(binding);
        assertEquals("tenant-1",       binding.tenantId());
        assertEquals("run-100",        binding.workflowRunId());
        assertEquals("research-agent", binding.nodeId());
        assertNotNull(binding.executionId());
        assertEquals("res-001", binding.reservationId());
    }

    @Test
    void executionId_isNotEqual_toWorkflowRunId() {
        // 6.17.3: WorkflowRunId ≠ ExecutionId
        AgentExecutionBinding binding = bridge.bindNodeExecution(
                "tenant-1", "run-100", "node-A", "res-001");

        assertNotEquals("run-100", binding.executionId().value(),
                "ExecutionId must never equal WorkflowRunId");
        assertNotEquals("node-A", binding.executionId().value(),
                "ExecutionId must never equal NodeId");
    }

    @Test
    void bindNodeExecution_idempotent_sameReservation() {
        AgentExecutionBinding first  = bridge.bindNodeExecution("t", "run-1", "node-1", "res-1");
        AgentExecutionBinding second = bridge.bindNodeExecution("t", "run-1", "node-1", "res-1");

        assertEquals(first.executionId(), second.executionId(),
                "Same node+run should produce the same ExecutionId (idempotent dispatch)");
        assertEquals(1, bridge.bindingCount());
    }

    @Test
    void findByExecutionId_returnsBinding() {
        AgentExecutionBinding binding = bridge.bindNodeExecution(
                "tenant-1", "run-200", "writer-node", "res-002");

        Optional<AgentExecutionBinding> found = bridge.findByExecutionId(binding.executionId());
        assertTrue(found.isPresent());
        assertEquals("writer-node", found.get().nodeId());
    }

    @Test
    void reconcileNode_returnsExistingBinding() {
        bridge.bindNodeExecution("t", "run-300", "analyze-node", "res-003");

        Optional<AgentExecutionBinding> reconciled = bridge.reconcileNode("t", "run-300", "analyze-node");
        assertTrue(reconciled.isPresent());
        assertEquals("analyze-node", reconciled.get().nodeId());
    }

    @Test
    void reconcileNode_unknownNode_returnsEmpty() {
        Optional<AgentExecutionBinding> result = bridge.reconcileNode("t", "run-X", "ghost-node");
        assertFalse(result.isPresent());
    }

    @Test
    void onExecutionCompleted_doesNotThrow() {
        AgentExecutionBinding binding = bridge.bindNodeExecution(
                "tenant-1", "run-400", "compute-node", "res-004");

        // Build a minimal real ExecutionState for the completed notification
        tech.kayys.andalus.harness.context.DefaultHarnessContext ctx =
                new tech.kayys.andalus.harness.context.DefaultHarnessContext(
                        tech.kayys.andalus.harness.context.DefaultHarnessIdentity.of("agent-test"),
                        tech.kayys.andalus.harness.context.DefaultHarnessSession.createNew(),
                        java.util.Map.of());

        tech.kayys.andalus.harness.execution.state.ExecutionState finalState =
                new tech.kayys.andalus.harness.execution.state.DefaultExecutionState(
                        binding.executionId(),
                        tech.kayys.andalus.harness.execution.state.ExecutionStatus.COMPLETED,
                        1L, ctx,
                        tech.kayys.andalus.harness.execution.state.ExecutionCursor.initial(),
                        tech.kayys.andalus.harness.execution.state.ExecutionData.empty());

        assertDoesNotThrow(() -> bridge.onExecutionCompleted(binding, finalState));
    }

    @Test
    void onExecutionFailed_doesNotThrow() {
        AgentExecutionBinding binding = bridge.bindNodeExecution(
                "tenant-1", "run-500", "fail-node", "res-005");

        assertDoesNotThrow(() -> bridge.onExecutionFailed(binding, "timeout after 30s"));
    }

    @Test
    void onExecutionCancelled_doesNotThrow() {
        AgentExecutionBinding binding = bridge.bindNodeExecution(
                "tenant-1", "run-600", "cancel-node", "res-006");

        assertDoesNotThrow(() -> bridge.onExecutionCancelled(binding));
    }

    @Test
    void multipleNodes_inSameRun_haveIndependentBindings() {
        AgentExecutionBinding a = bridge.bindNodeExecution("t", "run-700", "node-A", "res-A");
        AgentExecutionBinding b = bridge.bindNodeExecution("t", "run-700", "node-B", "res-B");

        assertNotEquals(a.executionId(), b.executionId(),
                "Each node gets its own ExecutionId");
        assertEquals(2, bridge.bindingCount());
    }
}
