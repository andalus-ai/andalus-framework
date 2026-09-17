package tech.kayys.wayang.execution.governance.audit;

/**
 * Service Provider Interface for publishing tool governance audit events.
 * Implementations may publish to an EventLedger, loggers, or messaging buses.
 */
@FunctionalInterface
public interface ToolAuditPublisher {

    /**
     * Publishes a tool audit event.
     *
     * @param event The immutable audit event.
     */
    void publish(ToolAuditEvent event);

    /**
     * No-op publisher for testing or non-audited local environments.
     */
    static ToolAuditPublisher noop() {
        return event -> {};
    }
}
