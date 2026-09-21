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


import tech.kayys.andalus.event.EventPayload;

/**
 * Runtime Events
 */
public interface RuntimeEvent extends EventPayload {
    
    record ExecutionStarted(
        String executionId,
        String definitionId,
        String definitionType
    ) implements RuntimeEvent {}
    
    record ExecutionCompleted(
        String executionId,
        long durationMs
    ) implements RuntimeEvent {}
    
    record ExecutionFailed(
        String executionId,
        String error
    ) implements RuntimeEvent {}
    
    record PhaseStarted(
        String executionId,
        String phase,
        String description
    ) implements RuntimeEvent {}
    
    record PhaseCompleted(
        String executionId,
        String phase,
        long durationMs
    ) implements RuntimeEvent {}
    
    record PhaseFailed(
        String executionId,
        String phase,
        String error
    ) implements RuntimeEvent {}
}