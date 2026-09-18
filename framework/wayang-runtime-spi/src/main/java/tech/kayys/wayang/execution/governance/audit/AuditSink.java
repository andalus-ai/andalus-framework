package tech.kayys.wayang.execution.governance.audit;

/**
 * SPI for durable audit logging of security events.
 */
@FunctionalInterface
public interface AuditSink {

    void append(SecurityEvent event);

    static AuditSink noop() {
        return event -> {};
    }
}
