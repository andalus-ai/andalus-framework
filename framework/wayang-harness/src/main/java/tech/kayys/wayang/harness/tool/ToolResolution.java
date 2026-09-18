package tech.kayys.wayang.harness.tool;

import java.util.Objects;

/**
 * Represents a tool resolution.
 *
 * <p>Its components capture `tool`, `reason`.</p>
 *
 * @param tool the tool
 * @param reason the reason
 */


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
