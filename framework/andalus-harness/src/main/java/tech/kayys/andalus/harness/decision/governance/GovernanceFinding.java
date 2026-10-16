package tech.kayys.andalus.harness.decision.governance;

import java.util.Map;

/**
 * A discrete finding reported during decision governance evaluation.
 */
public record GovernanceFinding(
        String code,
        GovernanceFindingSeverity severity,
        String source,
        String message,
        Map<String, Object> metadata
) {
    public GovernanceFinding {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code is required");
        }
        if (severity == null) {
            severity = GovernanceFindingSeverity.INFORMATION;
        }
        source = source == null ? "governance" : source;
        message = message == null ? "" : message;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static GovernanceFinding info(String code, String source, String message) {
        return new GovernanceFinding(code, GovernanceFindingSeverity.INFORMATION, source, message, Map.of());
    }

    public static GovernanceFinding warning(String code, String source, String message) {
        return new GovernanceFinding(code, GovernanceFindingSeverity.WARNING, source, message, Map.of());
    }

    public static GovernanceFinding blocking(String code, String source, String message) {
        return new GovernanceFinding(code, GovernanceFindingSeverity.BLOCKING, source, message, Map.of());
    }

    public static GovernanceFinding error(String code, String source, String message) {
        return new GovernanceFinding(code, GovernanceFindingSeverity.ERROR, source, message, Map.of());
    }
}
