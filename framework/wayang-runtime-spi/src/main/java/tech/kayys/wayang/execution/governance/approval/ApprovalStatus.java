package tech.kayys.wayang.execution.governance.approval;

/**
 * State lifecycle for human or automated approval requests.
 */
public enum ApprovalStatus {

    PENDING,

    APPROVED,

    REJECTED,

    EXPIRED,

    CANCELLED
}
