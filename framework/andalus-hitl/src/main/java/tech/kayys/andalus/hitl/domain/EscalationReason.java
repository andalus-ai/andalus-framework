package tech.kayys.andalus.hitl.domain;

/**
 * Defines the escalation reason values used by the Andalus framework.
 */


public enum EscalationReason {
    TIMEOUT,
    MANUAL,
    SLA_BREACH,
    PRIORITY_CHANGE
}