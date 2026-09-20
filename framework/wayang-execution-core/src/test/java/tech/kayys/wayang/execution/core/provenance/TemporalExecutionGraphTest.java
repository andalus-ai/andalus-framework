package tech.kayys.wayang.execution.core.provenance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.provenance.CausalContext;
import tech.kayys.wayang.execution.provenance.EdgeType;
import tech.kayys.wayang.execution.provenance.ExecutionEdge;
import tech.kayys.wayang.execution.provenance.ExecutionNode;
import tech.kayys.wayang.execution.provenance.NodeId;
import tech.kayys.wayang.execution.provenance.NodeType;
import tech.kayys.wayang.execution.provenance.ProvenanceChain;
import tech.kayys.wayang.execution.provenance.TemporalInterval;
import tech.kayys.wayang.execution.provenance.TraceId;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemporalExecutionGraphTest {

    @Test
    @DisplayName("TemporalExecutionGraph models nodes and causal edges accurately")
    void testExecutionGraph() {
        String execId = "exec-provenance-1";
        NodeId n1 = NodeId.of("node-workflow");
        NodeId n2 = NodeId.of("node-tool");
        NodeId n3 = NodeId.of("node-artifact");

        Instant t1 = Instant.now().minusSeconds(10);
        Instant t2 = Instant.now().minusSeconds(5);
        Instant t3 = Instant.now();

        ExecutionNode nodeWf = new ExecutionNode(n1, NodeType.WORKFLOW_STEP, execId, t1, Map.of("step", "compile"));
        ExecutionNode nodeTool = new ExecutionNode(n2, NodeType.TOOL_CALL, execId, t2, Map.of("tool", "javac"));
        ExecutionNode nodeArt = new ExecutionNode(n3, NodeType.ARTIFACT, execId, t3, Map.of("artifact", "app.jar"));

        DefaultExecutionGraph graph = new DefaultExecutionGraph("graph-1");
        graph.addNode(nodeWf)
                .addNode(nodeTool)
                .addNode(nodeArt);

        // Workflow TRIGGERED Tool
        graph.addEdge(new ExecutionEdge(n1, n2, EdgeType.TRIGGERED_BY, t2, Map.of()));
        // Tool PRODUCED Artifact
        graph.addEdge(new ExecutionEdge(n2, n3, EdgeType.PRODUCED, t3, Map.of()));

        assertEquals(3, graph.nodes().size());
        assertEquals(2, graph.edges().size());
        assertTrue(graph.findNode(n2).isPresent());

        var outgoing = graph.outgoingEdges(n2);
        assertEquals(1, outgoing.size());
        assertEquals(EdgeType.PRODUCED, outgoing.iterator().next().type());

        var incoming = graph.incomingEdges(n2);
        assertEquals(1, incoming.size());
        assertEquals(EdgeType.TRIGGERED_BY, incoming.iterator().next().type());
    }

    @Test
    @DisplayName("CausalContext and TemporalInterval helper tests")
    void testCausalContextAndInterval() {
        NodeId parent = NodeId.of("parent");
        NodeId trigger = NodeId.of("trigger");
        TraceId trace = TraceId.of("trace-123");

        CausalContext ctx = CausalContext.of("exec-42", parent, trigger, trace);
        assertEquals("exec-42", ctx.executionId());
        assertEquals(parent, ctx.parent());
        assertEquals(trigger, ctx.trigger());
        assertEquals(trace, ctx.trace());

        Instant now = Instant.now();
        TemporalInterval interval = TemporalInterval.between(now.minusSeconds(60), now.plusSeconds(60));
        assertTrue(interval.contains(now));
        assertFalse(interval.contains(now.minusSeconds(120)));
    }

    @Test
    @DisplayName("DefaultIntegrityVerifier validates chain order and hashes")
    void testIntegrityVerifier() {
        NodeId n1 = NodeId.of("n1");
        NodeId n2 = NodeId.of("n2");

        Instant t1 = Instant.parse("2026-09-20T08:00:00Z");
        Instant t2 = Instant.parse("2026-09-20T08:05:00Z");

        ExecutionNode node1 = new ExecutionNode(n1, NodeType.EXECUTION, "exec-chain", t1, Map.of());
        ExecutionNode node2 = new ExecutionNode(n2, NodeType.MODEL_CALL, "exec-chain", t2, Map.of());

        List<ExecutionNode> nodes = List.of(node1, node2);
        String digest = DefaultIntegrityVerifier.computeDigest(nodes);

        ProvenanceChain chain = new ProvenanceChain("exec-chain", nodes, digest);

        DefaultIntegrityVerifier verifier = new DefaultIntegrityVerifier();
        assertTrue(verifier.verifyChain(chain));

        // Corrupted digest fails
        ProvenanceChain badDigestChain = new ProvenanceChain("exec-chain", nodes, "sha256:corrupted");
        assertFalse(verifier.verifyChain(badDigestChain));

        // Inverted time sequence fails
        ExecutionNode node2Earlier = new ExecutionNode(n2, NodeType.MODEL_CALL, "exec-chain", t1.minusSeconds(10), Map.of());
        List<ExecutionNode> badTimeNodes = List.of(node1, node2Earlier);
        String badTimeDigest = DefaultIntegrityVerifier.computeDigest(badTimeNodes);
        ProvenanceChain badTimeChain = new ProvenanceChain("exec-chain", badTimeNodes, badTimeDigest);
        assertFalse(verifier.verifyChain(badTimeChain));
    }
}
