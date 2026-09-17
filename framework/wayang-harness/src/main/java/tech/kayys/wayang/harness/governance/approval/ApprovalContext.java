package tech.kayys.wayang.harness.governance.approval;

import java.util.Map;

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
