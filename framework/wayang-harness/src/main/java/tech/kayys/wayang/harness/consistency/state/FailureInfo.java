package tech.kayys.wayang.harness.consistency.state;

import java.util.Objects;

/**
 * Structured failure metadata supporting programmatic recovery policies.
 */
public record FailureInfo(
        FailureClass classification,
        String code,
        String message,
        boolean retryable,
        boolean compensatable
) {

    public FailureInfo {
        Objects.requireNonNull(classification, "FailureClass cannot be null");
        code = code != null ? code : "UNKNOWN_ERROR";
        message = message != null ? message : "";
    }

    public static FailureInfo of(FailureClass classification, String code, String message, boolean retryable, boolean compensatable) {
        return new FailureInfo(classification, code, message, retryable, compensatable);
    }

    public static FailureInfo transientError(String message) {
        return new FailureInfo(FailureClass.TRANSIENT, "TRANSIENT_ERROR", message, true, true);
    }

    public static FailureInfo terminal(String message) {
        return new FailureInfo(FailureClass.DATA_CORRUPTION, "TERMINAL_ERROR", message, false, false);
    }
}
