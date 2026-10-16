package tech.kayys.andalus.a2a.handoff;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A canonical agent handoff request.
 * Analogous to OpenAI Agents SDK agent handoffs — transfers execution
 * context and memory to a target agent, with explicit intent.
 */
public record AgentHandoff(
        String handoffId,
        String sourceAgentId,
        String targetAgentId,
        String targetSkill,
        HandoffContext context,
        HandoffReason reason,
        Instant initiatedAt,
        Map<String, Object> attributes
) {
    public AgentHandoff {
        Objects.requireNonNull(sourceAgentId, "sourceAgentId");
        Objects.requireNonNull(targetAgentId, "targetAgentId");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(reason, "reason");
        handoffId = handoffId == null ? UUID.randomUUID().toString() : handoffId;
        initiatedAt = initiatedAt == null ? Instant.now() : initiatedAt;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static AgentHandoff of(
            String sourceAgentId,
            String targetAgentId,
            HandoffContext context,
            HandoffReason reason) {
        return new AgentHandoff(UUID.randomUUID().toString(), sourceAgentId,
                targetAgentId, null, context, reason, Instant.now(), Map.of());
    }

    public static AgentHandoff toSkill(
            String sourceAgentId,
            String targetAgentId,
            String skill,
            HandoffContext context) {
        return new AgentHandoff(UUID.randomUUID().toString(), sourceAgentId,
                targetAgentId, skill, context, HandoffReason.SKILL_DELEGATION, Instant.now(), Map.of());
    }
}
