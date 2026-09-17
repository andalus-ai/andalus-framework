package tech.kayys.wayang.harness.governance.approval;

import java.util.Objects;
import java.util.UUID;

public record ApprovalId(String value) {
    public ApprovalId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Approval id must not be blank");
        }
    }

    public static ApprovalId generate() {
        return new ApprovalId("appr-" + UUID.randomUUID());
    }

    public static ApprovalId of(String value) {
        return new ApprovalId(value);
    }
}
