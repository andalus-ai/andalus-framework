package tech.kayys.wayang.spi.operator.authorization;

/**
 * Authorization scope distinguishing platform-wide access from tenant-bounded access.
 */
public enum OperatorScope {
    PLATFORM,
    TENANT
}
