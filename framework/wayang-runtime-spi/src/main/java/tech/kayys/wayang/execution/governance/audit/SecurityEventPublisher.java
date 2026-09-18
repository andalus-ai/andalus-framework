package tech.kayys.wayang.execution.governance.audit;

/**
 * SPI for broadcasting security and governance events.
 */
@FunctionalInterface
public interface SecurityEventPublisher {

    void publish(SecurityEvent event);

    static SecurityEventPublisher noop() {
        return event -> {};
    }
}
