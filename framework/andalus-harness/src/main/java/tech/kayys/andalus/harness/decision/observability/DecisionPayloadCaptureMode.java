package tech.kayys.andalus.harness.decision.observability;

/**
 * Controls how much decision payload is captured in observability records.
 *
 * <p>{@code METADATA} is the recommended default. {@code FULL} requires explicit
 * configuration, authorization, and retention rules.
 */
public enum DecisionPayloadCaptureMode {

    /**
     * No optional decision payload capture; only the event type and correlation IDs are recorded.
     */
    OFF,

    /**
     * Identifiers, event types, durations, statuses, and references only. Default.
     */
    METADATA,

    /**
     * Selected payload fields after configured redaction rules are applied.
     */
    REDACTED,

    /**
     * Full payload capture where explicitly permitted and subject to retention controls.
     */
    FULL
}
