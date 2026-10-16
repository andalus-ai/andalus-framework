package tech.kayys.andalus.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.communication.api.AgentRef;
import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.api.AgentResponse;
import tech.kayys.andalus.communication.capability.CapabilityId;
import tech.kayys.andalus.communication.message.MessagePayload;
import tech.kayys.andalus.execution.AgentInvocation;
import tech.kayys.andalus.execution.AgentResult;
import tech.kayys.andalus.execution.tree.ChildExecutionRequest;
import tech.kayys.andalus.execution.tree.ChildExecutionResult;
import tech.kayys.andalus.execution.tree.ExecutionNode;
import tech.kayys.andalus.security.delegation.DelegationAudience;
import tech.kayys.andalus.security.delegation.DelegationConstraints;
import tech.kayys.andalus.security.delegation.DelegationContext;
import tech.kayys.andalus.security.identity.Principal;
import tech.kayys.andalus.security.propagation.SecurityContextSnapshot;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionTreeTest {

    @Test
    void buildsAndQueriesExecutionTree() {
        ExecutionNode root = ExecutionNode.root("exec-root", "trace-1", Instant.now());
        assertTrue(root.isRoot());

        DefaultExecutionTree tree = new DefaultExecutionTree(root);
        assertEquals(root, tree.root());
        assertTrue(tree.contains("exec-root"));

        ExecutionNode child1 = ExecutionNode.child("exec-c1", "trace-1", "exec-root", Instant.now());
        ExecutionNode child2 = ExecutionNode.child("exec-c2", "trace-1", "exec-root", Instant.now());
        ExecutionNode child1Sub = ExecutionNode.child("exec-c1-sub", "trace-1", "exec-c1", Instant.now());

        tree.add(child1);
        tree.add(child2);
        tree.add(child1Sub);

        assertEquals(4, tree.nodes().size());
        assertEquals(2, tree.children("exec-root").size());
        assertEquals(List.of(child1Sub), tree.children("exec-c1"));
        assertEquals(Optional.of(child1), tree.parent("exec-c1-sub"));
        assertEquals(Optional.of(root), tree.parent("exec-c1"));
    }

    @Test
    void rejectsInvalidNodesInTree() {
        ExecutionNode childWithoutParentInTree = ExecutionNode.child("exec-orphan", "trace-1", "non-existent", Instant.now());
        ExecutionNode root = ExecutionNode.root("exec-root", "trace-1", Instant.now());
        DefaultExecutionTree tree = new DefaultExecutionTree(root);

        assertThrows(IllegalArgumentException.class, () -> tree.add(childWithoutParentInTree));
    }

    @Test
    void childExecutionManagerExecutesChild() {
        DefaultAgentInvocationFactory factory = new DefaultAgentInvocationFactory(
                new DefaultExecutionIdGenerator(),
                new DefaultCancellationTokenFactory()
        );
        SecurityContextSnapshot security = new SecurityContextSnapshot(
                Principal.anonymous(), TenantContext.empty(), Optional.empty(), Map.of()
        );
        AgentRequest parentReq = AgentRequest.of(
                AgentRef.local("parent-agent", "Parent", "rt-1"),
                CapabilityId.of("cap.parent"),
                MessagePayload.text("parent")
        );
        AgentInvocation parent = factory.root(parentReq, security);

        DefaultChildExecutionManager manager = new DefaultChildExecutionManager(
                factory,
                invocation -> CompletableFuture.completedFuture(
                        AgentResult.of(AgentResponse.success(MessagePayload.text("child done")))
                )
        );

        ChildExecutionRequest req = ChildExecutionRequest.of(
                AgentRequest.of(
                        AgentRef.local("child-agent", "Child", "rt-1"),
                        CapabilityId.of("cap.child"),
                        MessagePayload.text("child")
                ),
                DelegationContext.create(DelegationAudience.any(), DelegationConstraints.unrestricted(), 3600)
        );

        ChildExecutionResult result = manager.execute(parent, req).toCompletableFuture().join();
        assertNotNull(result);
        assertEquals(parent.context().metadata().executionId(), result.parentExecutionId());
        assertEquals(parent.context().metadata().traceId(), result.traceId());
        assertTrue(result.successful());
    }
}
