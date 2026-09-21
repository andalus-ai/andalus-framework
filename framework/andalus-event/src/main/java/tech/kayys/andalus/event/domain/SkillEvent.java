package tech.kayys.andalus.event.domain;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.event.EventPayload;
import tech.kayys.andalus.skill.spi.SkillDefinition;
import tech.kayys.andalus.resource.Artifact;

/**
 * Skill Events
 */
public interface SkillEvent extends EventPayload {
    
    record SkillStarted(
        SkillDefinition skill,
        String executionId,
        Map<String, Object> inputs
    ) implements SkillEvent {}
    
    record SkillCompleted(
        String executionId,
        List<Artifact> outputs,
        long durationMs
    ) implements SkillEvent {}
    
    record SkillFailed(
        String executionId,
        String error
    ) implements SkillEvent {}
    
    record SkillRetrying(
        String executionId,
        int attempt,
        int maxAttempts,
        String reason
    ) implements SkillEvent {}
}