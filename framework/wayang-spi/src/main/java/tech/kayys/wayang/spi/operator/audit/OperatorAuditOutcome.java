package tech.kayys.wayang.spi.operator.audit;

/**
 * Outcome status of an operator action.
 */
public enum OperatorAuditOutcome {
    AUTHORIZED,
    DENIED,
    SUCCESS,
    FAILURE,
    ERROR
}
