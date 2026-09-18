package tech.kayys.wayang.harness.journal;

/**
 * Outcome states accounting for uncertainty in external side-effects across crashes.
 */
public enum OutcomeState {
    NOT_STARTED,
    STARTED,
    SUCCEEDED,
    FAILED,
    UNKNOWN
}
