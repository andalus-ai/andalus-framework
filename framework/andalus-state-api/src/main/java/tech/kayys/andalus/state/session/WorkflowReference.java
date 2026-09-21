package tech.kayys.andalus.state.session;

/**
 * Decoupled reference to a Gamelan workflow execution.
 * Andalus references Gamelan for workflow continuity without absorbing its state machine.
 */
public record WorkflowReference(
        String workflowId,
        String workflowVersion,
        String checkpointReference) {

    public static WorkflowReference of(String workflowId, String workflowVersion, String checkpointReference) {
        return new WorkflowReference(workflowId, workflowVersion, checkpointReference);
    }
}
