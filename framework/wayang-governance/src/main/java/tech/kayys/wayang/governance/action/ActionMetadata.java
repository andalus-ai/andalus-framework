package tech.kayys.wayang.governance.action;

import java.util.Map;
import java.util.Objects;

/**
 * Metadata classifying risk, idempotency, and environmental characteristics of a {@link HarnessAction}.
 */
public record ActionMetadata(
        boolean mutatesWorkspace,
        boolean requiresNetwork,
        boolean idempotent,
        String riskLevel,
        Map<String, Object> attributes
) {

    public ActionMetadata {
        riskLevel = riskLevel == null ? "LOW" : riskLevel;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static ActionMetadata readOnly() {
        return new ActionMetadata(false, false, true, "LOW", Map.of());
    }

    public static ActionMetadata mutating(String riskLevel) {
        return new ActionMetadata(true, false, false, riskLevel, Map.of());
    }

    public static ActionMetadata network(String riskLevel) {
        return new ActionMetadata(false, true, false, riskLevel, Map.of());
    }
}
