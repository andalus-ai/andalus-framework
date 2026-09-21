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

import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.resource.Artifact;

/**
 * Workflow Step Result
 */
public record WorkflowStepResult(
    String id,
    String stepId,
    String stepName,
    String status,
    List<Artifact> outputs,
    long durationMs,
    String error
) {
    public static WorkflowStepResult success(String stepId, String stepName) {
        return new WorkflowStepResult(
            Id.random().asString(),
            stepId,
            stepName,
            "COMPLETED",
            List.of(),
            0,
            null
        );
    }
    
    public static WorkflowStepResult failed(String stepId, String stepName, String error) {
        return new WorkflowStepResult(
            Id.random().asString(),
            stepId,
            stepName,
            "FAILED",
            List.of(),
            0,
            error
        );
    }
}
