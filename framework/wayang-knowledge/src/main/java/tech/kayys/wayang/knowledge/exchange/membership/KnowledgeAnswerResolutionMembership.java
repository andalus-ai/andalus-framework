package tech.kayys.wayang.knowledge.exchange.membership;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a knowledge answer resolution membership.
 *
 * <p>Its components capture `runtime id`, `tenant id`, `workspace id`, `project id`, `state`, and other values.</p>
 *
 * @param runtimeId the runtime id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param state the state
 * @param effectiveFrom the effective from
 * @param effectiveUntil the effective until
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionMembership(
        String runtimeId,
        String tenantId,
        String workspaceId,
        String projectId,
        KnowledgeAnswerResolutionMembershipState state,
        Instant effectiveFrom,
        Instant effectiveUntil,
        String metadata
) {
    public KnowledgeAnswerResolutionMembership {
        Objects.requireNonNull(runtimeId, "runtimeId");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(effectiveFrom, "effectiveFrom");
    }

    public boolean activeAt(Instant instant) {
        return state == KnowledgeAnswerResolutionMembershipState.ACTIVE
                && !instant.isBefore(effectiveFrom)
                && (effectiveUntil == null || instant.isBefore(effectiveUntil));
    }
}
