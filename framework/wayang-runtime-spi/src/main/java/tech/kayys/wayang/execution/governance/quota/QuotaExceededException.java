package tech.kayys.wayang.execution.governance.quota;

/**
 * Thrown when a tool execution violates quota, rate limits, or budget constraints.
 */
public final class QuotaExceededException extends RuntimeException {

    private final String reason;
    private final String resourceKey;

    public QuotaExceededException(String message, String reason, String resourceKey) {
        super(message);
        this.reason = reason;
        this.resourceKey = resourceKey;
    }

    public String reason() {
        return reason;
    }

    public String resourceKey() {
        return resourceKey;
    }
}
