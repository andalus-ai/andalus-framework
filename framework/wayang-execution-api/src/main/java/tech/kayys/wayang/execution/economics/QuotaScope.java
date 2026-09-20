package tech.kayys.wayang.execution.economics;

/**
 * Organizational scope within which a quota applies.
 */
public enum QuotaScope {
    TENANT,
    ORGANIZATION,
    USER,
    SESSION
}
