package tech.kayys.andalus.harness.decision.governance;

import java.util.Map;

/**
 * An obligation that must be fulfilled before or during action execution.
 */
public record GovernanceObligation(
        String obligationType,
        String description,
        Map<String, Object> parameters
) {
    public GovernanceObligation {
        if (obligationType == null || obligationType.isBlank()) {
            throw new IllegalArgumentException("obligationType is required");
        }
        description = description == null ? "" : description;
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }

    public static GovernanceObligation hitlApproval(String approverRole, String reason) {
        return new GovernanceObligation(
                "HITL_APPROVAL",
                reason,
                Map.of("approverRole", approverRole, "reason", reason)
        );
    }
}
