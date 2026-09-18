package tech.kayys.wayang.hitl.domain;

/**
 * Defines the human task status values used by the Wayang framework.
 */


public enum HumanTaskStatus {
    CREATED,
    ASSIGNED,
    IN_PROGRESS,
    ESCALATED,
    COMPLETED,
    CANCELLED,
    EXPIRED;

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == EXPIRED;
    }
}