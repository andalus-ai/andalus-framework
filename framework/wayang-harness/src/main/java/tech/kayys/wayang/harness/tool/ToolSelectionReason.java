package tech.kayys.wayang.harness.tool;

public record ToolSelectionReason(
        String strategy,
        String message
) {
    public static ToolSelectionReason direct(String message) {
        return new ToolSelectionReason("DIRECT", message);
    }

    public static ToolSelectionReason capability(String capability) {
        return new ToolSelectionReason("CAPABILITY_MATCH", "Matched capability: " + capability);
    }

    public static ToolSelectionReason policy(String policyName) {
        return new ToolSelectionReason("POLICY_SELECTION", "Selected by policy: " + policyName);
    }
}
