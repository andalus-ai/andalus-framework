package tech.kayys.andalus.harness.state;

/**
 * Justification for proposing, updating, disputing, or retracting an agent belief.
 */
public enum BeliefUpdateReason {
    NEW_EVIDENCE,
    CONTRADICTING_EVIDENCE,
    HUMAN_CORRECTION,
    VERIFICATION,
    EXPLICIT_RETRACTION,
    REASSESSMENT
}
