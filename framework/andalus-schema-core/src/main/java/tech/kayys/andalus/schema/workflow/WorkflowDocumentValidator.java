package tech.kayys.andalus.schema.workflow;

import tech.kayys.andalus.schema.canvas.CanvasData;
import tech.kayys.andalus.schema.canvas.CanvasNode;
import tech.kayys.andalus.schema.node.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Performs deterministic, dependency-free structural validation of a workflow
 * document. Semantic validation (node configuration, ports and permissions)
 * belongs to capability-aware framework/runtime services.
 */
public class WorkflowDocumentValidator {

    public WorkflowValidationResult validate(WorkflowDocument document) {
        List<WorkflowDiagnostic> diagnostics = new ArrayList<>();
        if (document == null) {
            add(diagnostics, "WORKFLOW_DOCUMENT_REQUIRED", WorkflowDiagnosticSeverity.ERROR,
                    "", "Workflow document is required.");
            return new WorkflowValidationResult(diagnostics);
        }

        if (document.getSchemaVersion() != WorkflowDocument.CURRENT_SCHEMA_VERSION) {
            add(diagnostics, "WORKFLOW_SCHEMA_VERSION_UNSUPPORTED", WorkflowDiagnosticSeverity.ERROR,
                    "/schemaVersion", "Unsupported workflow document schema version: " + document.getSchemaVersion());
        }

        WorkflowSpec workflow = document.getWorkflow();
        if (workflow == null) {
            add(diagnostics, "WORKFLOW_DEFINITION_REQUIRED", WorkflowDiagnosticSeverity.ERROR,
                    "/workflow", "Executable workflow definition is required.");
            return new WorkflowValidationResult(diagnostics);
        }

        List<Node> nodes = workflow.getNodes() == null ? new ArrayList<>() : workflow.getNodes();
        List<Connection> connections = workflow.getConnections() == null
                ? new ArrayList<>() : workflow.getConnections();
        Set<String> nodeIds = new HashSet<>();

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            String pointer = "/workflow/nodes/" + i;
            if (node == null) {
                add(diagnostics, "WORKFLOW_NODE_REQUIRED", WorkflowDiagnosticSeverity.ERROR,
                        pointer, "Workflow node must not be null.");
                continue;
            }
            String id = node.getMetadata() == null ? null : node.getMetadata().getId();
            if (isBlank(id)) {
                add(diagnostics, "WORKFLOW_NODE_ID_REQUIRED", WorkflowDiagnosticSeverity.ERROR,
                        pointer + "/metadata/id", "Workflow node metadata.id is required.");
            } else if (!nodeIds.add(id)) {
                add(diagnostics, "WORKFLOW_NODE_ID_DUPLICATE", WorkflowDiagnosticSeverity.ERROR,
                        pointer + "/metadata/id", "Duplicate workflow node id: " + id);
            }
        }

        String entryNodeId = workflow.getEntryNodeId();
        if (isBlank(entryNodeId)) {
            add(diagnostics, "WORKFLOW_ENTRY_NODE_REQUIRED", WorkflowDiagnosticSeverity.ERROR,
                    "/workflow/entryNodeId", "Workflow entryNodeId is required.");
        } else if (!nodeIds.contains(entryNodeId)) {
            add(diagnostics, "WORKFLOW_ENTRY_NODE_NOT_FOUND", WorkflowDiagnosticSeverity.ERROR,
                    "/workflow/entryNodeId", "Entry node does not exist: " + entryNodeId);
        }

        List<String> exitNodeIds = workflow.getExitNodeIds() == null
                ? new ArrayList<>() : workflow.getExitNodeIds();
        for (int i = 0; i < exitNodeIds.size(); i++) {
            String exitId = exitNodeIds.get(i);
            if (isBlank(exitId) || !nodeIds.contains(exitId)) {
                add(diagnostics, "WORKFLOW_EXIT_NODE_NOT_FOUND", WorkflowDiagnosticSeverity.ERROR,
                        "/workflow/exitNodeIds/" + i, "Exit node does not exist: " + exitId);
            }
        }

        for (int i = 0; i < connections.size(); i++) {
            Connection connection = connections.get(i);
            String pointer = "/workflow/connections/" + i;
            if (connection == null) {
                add(diagnostics, "WORKFLOW_CONNECTION_REQUIRED", WorkflowDiagnosticSeverity.ERROR,
                        pointer, "Workflow connection must not be null.");
                continue;
            }
            if (isBlank(connection.getFromNodeId()) || !nodeIds.contains(connection.getFromNodeId())) {
                add(diagnostics, "WORKFLOW_CONNECTION_SOURCE_NOT_FOUND", WorkflowDiagnosticSeverity.ERROR,
                        pointer + "/fromNodeId", "Connection source node does not exist: " + connection.getFromNodeId());
            }
            if (isBlank(connection.getToNodeId()) || !nodeIds.contains(connection.getToNodeId())) {
                add(diagnostics, "WORKFLOW_CONNECTION_TARGET_NOT_FOUND", WorkflowDiagnosticSeverity.ERROR,
                        pointer + "/toNodeId", "Connection target node does not exist: " + connection.getToNodeId());
            }
        }

        validateCanvas(document.getCanvas(), nodeIds, diagnostics);
        return new WorkflowValidationResult(diagnostics);
    }

    private void validateCanvas(CanvasData canvas, Set<String> workflowNodeIds,
            List<WorkflowDiagnostic> diagnostics) {
        if (canvas == null) {
            add(diagnostics, "WORKFLOW_CANVAS_MISSING", WorkflowDiagnosticSeverity.WARNING,
                    "/canvas", "Canvas presentation data is not provided.");
            return;
        }

        Set<String> canvasNodeIds = new HashSet<>();
        List<CanvasNode> canvasNodes = canvas.nodes == null ? new ArrayList<>() : canvas.nodes;
        for (int i = 0; i < canvasNodes.size(); i++) {
            CanvasNode node = canvasNodes.get(i);
            String pointer = "/canvas/nodes/" + i;
            if (node == null || isBlank(node.id)) {
                add(diagnostics, "CANVAS_NODE_ID_REQUIRED", WorkflowDiagnosticSeverity.ERROR,
                        pointer + "/id", "Canvas node id is required.");
                continue;
            }
            if (!canvasNodeIds.add(node.id)) {
                add(diagnostics, "CANVAS_NODE_ID_DUPLICATE", WorkflowDiagnosticSeverity.ERROR,
                        pointer + "/id", "Duplicate canvas node id: " + node.id);
            }
            if (!workflowNodeIds.contains(node.id)) {
                add(diagnostics, "CANVAS_NODE_WORKFLOW_NODE_NOT_FOUND", WorkflowDiagnosticSeverity.WARNING,
                        pointer + "/id", "Canvas node has no matching executable workflow node: " + node.id);
            }
            if (node.position != null
                    && (!Double.isFinite(node.position.x) || !Double.isFinite(node.position.y))) {
                add(diagnostics, "CANVAS_NODE_POSITION_INVALID", WorkflowDiagnosticSeverity.ERROR,
                        pointer + "/position", "Canvas node coordinates must be finite numbers.");
            }
        }

        List<String> missingVisualNodes = new ArrayList<>();
        for (String workflowNodeId : workflowNodeIds) {
            if (!canvasNodeIds.contains(workflowNodeId)) {
                missingVisualNodes.add(workflowNodeId);
            }
        }
        for (String missingId : missingVisualNodes) {
            add(diagnostics, "CANVAS_NODE_LAYOUT_MISSING", WorkflowDiagnosticSeverity.WARNING,
                    "/canvas/nodes", "No canvas presentation node exists for workflow node: " + missingId);
        }
    }

    private void add(List<WorkflowDiagnostic> diagnostics, String code,
            WorkflowDiagnosticSeverity severity, String pointer, String message) {
        diagnostics.add(new WorkflowDiagnostic(code, severity, pointer, message));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
