package tech.kayys.wayang.harness.tool;

import java.util.Objects;

public record ToolResolution(
        ToolDescriptor tool,
        ToolSelectionReason reason
) {
    public ToolResolution {
        Objects.requireNonNull(tool, "ToolDescriptor cannot be null");
        if (reason == null) {
            reason = ToolSelectionReason.direct("Default resolution");
        }
    }
}
