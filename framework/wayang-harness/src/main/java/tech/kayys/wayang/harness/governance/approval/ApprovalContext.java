package tech.kayys.wayang.harness.governance.approval;

import java.util.Map;

/**
 * Represents a approval context.
 *
 * <p>Its components capture `requested by`, `metadata`.</p>
 *
 * @param requestedBy the requested by
 * @param metadata the metadata
 */


public record ApprovalContext(
        String requestedBy,
        Map<String, Object> metadata
) {
    public ApprovalContext {
        requestedBy = requestedBy == null ? "unknown" : requestedBy;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ApprovalContext of(String requestedBy) {
        return new ApprovalContext(requestedBy, Map.of());
    }
}
