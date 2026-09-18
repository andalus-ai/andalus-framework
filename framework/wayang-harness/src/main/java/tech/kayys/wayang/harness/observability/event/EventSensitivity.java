package tech.kayys.wayang.harness.observability.event;

/**
 * Sensitivity classification for event confidentiality and audit redaction.
 */
public enum EventSensitivity {
    PUBLIC,
    INTERNAL,
    CONFIDENTIAL,
    SECRET
}
