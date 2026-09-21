package tech.kayys.andalus.state.session;

import java.util.Objects;

/**
 * First-class participant in a collaborative, multi-agent session.
 */
public record SessionParticipant(
        String participantId,
        String agentId,
        String role,
        String capabilityScope) {

    public SessionParticipant {
        Objects.requireNonNull(participantId, "participantId must not be null");
        agentId = agentId != null ? agentId : "unknown-agent";
        role = role != null ? role : "collaborator";
        capabilityScope = capabilityScope != null ? capabilityScope : "standard";
    }

    public static SessionParticipant of(String participantId, String agentId, String role, String capabilityScope) {
        return new SessionParticipant(participantId, agentId, role, capabilityScope);
    }
}
