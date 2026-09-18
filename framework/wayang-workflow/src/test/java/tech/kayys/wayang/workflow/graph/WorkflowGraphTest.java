package tech.kayys.wayang.workflow.graph;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WorkflowGraphTest {

    @Test
    void testExecutionGraphConstructionAndTopology() {
        NodeId startId = NodeId.of("start");
        NodeId procId = NodeId.of("process");
        NodeId endId = NodeId.of("end");

        ExecutionNode startNode = DefaultExecutionNode.of(startId, NodeType.OPERATION);
        ExecutionNode procNode = DefaultExecutionNode.of(procId, NodeType.OPERATION);
        ExecutionNode endNode = DefaultExecutionNode.of(endId, NodeType.OPERATION);

        ExecutionEdge edge1 = DefaultExecutionEdge.dependsOn(startId, procId, DependencySemantics.HARD);
        ExecutionEdge edge2 = DefaultExecutionEdge.dependsOn(procId, endId, DependencySemantics.HARD);

        ExecutionGraph graph = DefaultExecutionGraph.builder()
                .id(ExecutionGraphId.of("graph-1"))
                .addNode(startNode)
                .addNode(procNode)
                .addNode(endNode)
                .addEdge(edge1)
                .addEdge(edge2)
                .build();

        assertEquals(3, graph.nodes().size());
        assertEquals(2, graph.edges().size());

        assertEquals(1, graph.outgoingEdges(startId).size());
        assertEquals(procId, graph.outgoingEdges(startId).iterator().next().to());

        assertEquals(1, graph.incomingEdges(procId).size());
        assertEquals(startId, graph.incomingEdges(procId).iterator().next().from());
    }

    @Test
    void testGraphSchedulerReadySet() {
        NodeId n1 = NodeId.of("n1");
        NodeId n2 = NodeId.of("n2");
        NodeId n3 = NodeId.of("n3");

        ExecutionNode node1 = DefaultExecutionNode.of(n1, NodeType.OPERATION);
        ExecutionNode node2 = DefaultExecutionNode.of(n2, NodeType.OPERATION);
        ExecutionNode node3 = DefaultExecutionNode.of(n3, NodeType.OPERATION);

        ExecutionEdge e1 = DefaultExecutionEdge.dependsOn(n1, n2, DependencySemantics.HARD);
        ExecutionEdge e2 = DefaultExecutionEdge.dependsOn(n2, n3, DependencySemantics.HARD);

        ExecutionGraph graph = DefaultExecutionGraph.builder()
                .id(ExecutionGraphId.of("graph-sched"))
                .addNode(node1)
                .addNode(node2)
                .addNode(node3)
                .addEdge(e1)
                .addEdge(e2)
                .build();

        GraphScheduler scheduler = new DefaultGraphScheduler();

        // Initially, node1 has no incoming edges, so it should be ready
        ReadySet ready = scheduler.computeReadySet(graph, Map.of());
        assertEquals(Set.of(node1), ready.readyNodes());
        assertTrue(ready.hasReadyNodes());

        // When node1 completed, node2 should be ready
        ready = scheduler.computeReadySet(graph, Map.of(n1, NodeState.COMPLETED));
        assertEquals(Set.of(node2), ready.readyNodes());

        // When node1 and node2 completed, node3 should be ready
        ready = scheduler.computeReadySet(graph, Map.of(n1, NodeState.COMPLETED, n2, NodeState.COMPLETED));
        assertEquals(Set.of(node3), ready.readyNodes());

        // When all completed, ready should be empty
        ready = scheduler.computeReadySet(graph, Map.of(n1, NodeState.COMPLETED, n2, NodeState.COMPLETED, n3, NodeState.COMPLETED));
        assertFalse(ready.hasReadyNodes());

        // Check if graph finished
        assertTrue(scheduler.isGraphFinished(graph, Map.of(n1, NodeState.COMPLETED, n2, NodeState.COMPLETED, n3, NodeState.COMPLETED)));
        assertEquals(GraphState.COMPLETED, scheduler.deriveGraphState(graph, Map.of(n1, NodeState.COMPLETED, n2, NodeState.COMPLETED, n3, NodeState.COMPLETED)));
    }

    @Test
    void testConditionEvaluationAndLoopPolicy() {
        ConditionExpression expr = ConditionExpression.equals("status", "SUCCESS");
        assertTrue(expr.evaluate(Map.of("status", "SUCCESS")));
        assertFalse(expr.evaluate(Map.of("status", "FAILURE")));
        assertFalse(expr.evaluate(Map.of()));

        ConditionNode condNode = new ConditionNode(
                NodeId.of("cond-1"),
                expr,
                BranchSet.binary(NodeId.of("success-branch"), NodeId.of("fail-branch")),
                null
        );
        assertEquals(Optional.of(NodeId.of("success-branch")), condNode.branches().select("true"));
        assertEquals(Optional.of(NodeId.of("fail-branch")), condNode.branches().select("false"));

        LoopPolicy loop = LoopPolicy.maxIterations(5);
        assertEquals(5, loop.maxIterations());
        assertEquals(LoopBackoff.none(), loop.backoff());
    }
}
