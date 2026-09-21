package tech.kayys.andalus.event.domain;
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

import tech.kayys.andalus.event.EventPayload;
import tech.kayys.andalus.resource.Artifact;

/**
 * Tool Events
 */
public interface ToolEvent extends EventPayload {
    
    record ToolDiscovered(
        String toolId,
        String toolName,
        Map<String, Object> capabilities
    ) implements ToolEvent {}
    
    record ToolInvoked(
        String toolId,
        String toolName,
        Map<String, Object> inputs
    ) implements ToolEvent {}
    
    record ToolCompleted(
        String toolId,
        String executionId,
        List<Artifact> outputs,
        long durationMs
    ) implements ToolEvent {}
    
    record ToolFailed(
        String toolId,
        String executionId,
        String error
    ) implements ToolEvent {}
    
    record ToolTimeout(
        String toolId,
        String executionId,
        long timeoutMs
    ) implements ToolEvent {}
}