package tech.kayys.wayang.hitl.domain;

/**
 * Defines the escalation reason values used by the Wayang framework.
 */


public enum EscalationReason {
    TIMEOUT,
    MANUAL,
    SLA_BREACH,
    PRIORITY_CHANGE
}