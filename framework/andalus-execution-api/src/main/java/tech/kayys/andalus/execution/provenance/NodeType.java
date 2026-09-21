package tech.kayys.andalus.execution.provenance;

/**
 * Standard node types in the Temporal Execution Graph.
 */
public enum NodeType {
    EXECUTION,
    WORKFLOW,
    WORKFLOW_STEP,
    DECISION,
    POLICY_DECISION,
    MODEL_CALL,
    TOOL_CALL,
    RESOURCE,
    RESOURCE_BINDING,
    CHECKPOINT,
    MIGRATION,
    EFFECT,
    ARTIFACT,
    APPROVAL,
    FAILURE,
    RECOVERY,
    RUNTIME
}
