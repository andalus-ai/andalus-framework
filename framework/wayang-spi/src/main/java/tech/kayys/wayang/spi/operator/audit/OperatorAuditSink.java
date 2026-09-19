package tech.kayys.wayang.spi.operator.audit;

/**
 * SPI sink for receiving and persisting operator audit events.
 */
public interface OperatorAuditSink {

    void publish(OperatorAuditEvent event);
}
