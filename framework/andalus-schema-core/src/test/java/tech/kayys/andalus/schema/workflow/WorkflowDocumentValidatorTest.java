package tech.kayys.andalus.schema.workflow;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.schema.canvas.CanvasData;
import tech.kayys.andalus.schema.canvas.CanvasNode;
import tech.kayys.andalus.schema.common.Metadata;
import tech.kayys.andalus.schema.layout.Position;
import tech.kayys.andalus.schema.node.Node;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowDocumentValidatorTest {

    private final WorkflowDocumentValidator validator = new WorkflowDocumentValidator();

    @Test
    void acceptsWorkflowWithMatchingCanvasNodes() {
        WorkflowSpec workflow = workflow(node("start"), node("finish"));
        workflow.setEntryNodeId("start");
        workflow.setExitNodeIds(List.of("finish"));
        workflow.setConnections(List.of(new Connection("start", "finish", "out", "in", null)));

        CanvasData canvas = new CanvasData();
        canvas.nodes.add(canvasNode("start"));
        canvas.nodes.add(canvasNode("finish"));

        WorkflowValidationResult result = validator.validate(
                new WorkflowDocument("doc-1", "Example", workflow, canvas));

        assertThat(result.isValid()).isTrue();
        assertThat(result.getDiagnostics()).isEmpty();
    }

    @Test
    void reportsMissingAndDuplicateNodeIdsAndInvalidReferences() {
        WorkflowSpec workflow = workflow(node("start"), node("start"));
        workflow.setEntryNodeId("missing");
        workflow.setExitNodeIds(List.of("also-missing"));
        workflow.setConnections(List.of(new Connection("missing-source", "missing-target", null, null, null)));

        WorkflowValidationResult result = validator.validate(
                new WorkflowDocument("doc-2", "Invalid", workflow, new CanvasData()));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getDiagnostics())
                .extracting(WorkflowDiagnostic::getCode)
                .contains(
                        "WORKFLOW_NODE_ID_DUPLICATE",
                        "WORKFLOW_ENTRY_NODE_NOT_FOUND",
                        "WORKFLOW_EXIT_NODE_NOT_FOUND",
                        "WORKFLOW_CONNECTION_SOURCE_NOT_FOUND",
                        "WORKFLOW_CONNECTION_TARGET_NOT_FOUND");
        assertThat(result.getErrorCount()).isGreaterThanOrEqualTo(5);
    }

    @Test
    void treatsMissingCanvasLayoutAsWarningRatherThanExecutionError() {
        WorkflowSpec workflow = workflow(node("start"));
        workflow.setEntryNodeId("start");

        WorkflowValidationResult result = validator.validate(
                new WorkflowDocument("doc-3", "No canvas", workflow, new CanvasData()));

        assertThat(result.isValid()).isTrue();
        assertThat(result.getWarningCount()).isEqualTo(1);
        assertThat(result.getDiagnostics().get(0).getCode()).isEqualTo("CANVAS_NODE_LAYOUT_MISSING");
    }

    @Test
    void rejectsNonFiniteCanvasCoordinates() {
        WorkflowSpec workflow = workflow(node("start"));
        workflow.setEntryNodeId("start");

        CanvasData canvas = new CanvasData();
        CanvasNode visualNode = canvasNode("start");
        visualNode.position = new Position(Double.NaN, 10);
        canvas.nodes.add(visualNode);

        WorkflowValidationResult result = validator.validate(
                new WorkflowDocument("doc-4", "Invalid canvas", workflow, canvas));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getDiagnostics())
                .extracting(WorkflowDiagnostic::getCode)
                .contains("CANVAS_NODE_POSITION_INVALID");
    }

    @Test
    void reportsMissingDocumentAndWorkflow() {
        assertThat(validator.validate(null).getDiagnostics())
                .extracting(WorkflowDiagnostic::getCode)
                .containsExactly("WORKFLOW_DOCUMENT_REQUIRED");

        WorkflowDocument document = new WorkflowDocument();
        assertThat(validator.validate(document).getDiagnostics())
                .extracting(WorkflowDiagnostic::getCode)
                .containsExactly("WORKFLOW_DEFINITION_REQUIRED");
    }

    private WorkflowSpec workflow(Node... nodes) {
        WorkflowSpec workflow = new WorkflowSpec();
        workflow.setNodes(List.of(nodes));
        return workflow;
    }

    private Node node(String id) {
        Metadata metadata = new Metadata();
        metadata.setId(id);
        return new Node(metadata, "task", null, null, null, null, null);
    }

    private CanvasNode canvasNode(String id) {
        CanvasNode node = new CanvasNode();
        node.id = id;
        node.position = new Position(10, 20);
        return node;
    }
}
