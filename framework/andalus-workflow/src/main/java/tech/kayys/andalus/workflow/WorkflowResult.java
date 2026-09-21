package tech.kayys.andalus.workflow;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.List;
import java.util.Map;

import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.resource.Artifact;


/**
 * Workflow Result - complete result model
 */
public record WorkflowResult(
    String id,
    String workflowId,
    String status,
    List<Artifact> outputs,
    Map<String, Object> metadata,
    long durationMs,
    long startTime,
    long endTime,
    String error,
    List<WorkflowStepResult> stepResults
) {
    public static WorkflowResult success(String workflowId, List<Artifact> outputs) {
        long now = System.currentTimeMillis();
        return new WorkflowResult(
            Id.random().asString(),
            workflowId,
            "COMPLETED",
            outputs,
            Map.of(),
            0,
            now,
            now,
            null,
            List.of()
        );
    }
    
    public static WorkflowResult failed(String workflowId, String error) {
        long now = System.currentTimeMillis();
        return new WorkflowResult(
            Id.random().asString(),
            workflowId,
            "FAILED",
            List.of(),
            Map.of(),
            0,
            now,
            now,
            error,
            List.of()
        );
    }
    
    public WorkflowResult withStepResult(WorkflowStepResult stepResult) {
        List<WorkflowStepResult> newResults = new ArrayList<>(stepResults);
        newResults.add(stepResult);
        return new WorkflowResult(id, workflowId, status, outputs, metadata, 
            durationMs, startTime, endTime, error, newResults);
    }
}
