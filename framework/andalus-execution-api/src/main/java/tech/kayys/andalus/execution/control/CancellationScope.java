package tech.kayys.andalus.execution.control;

import java.util.Objects;

/**
 * Defines scope and cascade behavior for execution cancellation.
 */
public record CancellationScope(
        String scopeId,
        boolean cascadeToChildren,
        String reason
) {
    public CancellationScope {
        Objects.requireNonNull(scopeId, "scopeId cannot be null");
        reason = reason == null ? "Cancelled by user or policy" : reason;
    }

    public static CancellationScope of(String scopeId) {
        return new CancellationScope(scopeId, true, "Cancelled");
    }

    public static CancellationScope of(String scopeId, String reason) {
        return new CancellationScope(scopeId, true, reason);
    }
}
