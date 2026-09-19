package tech.kayys.wayang.spi.operator.tool;

import tech.kayys.wayang.spi.operator.OperatorPermission;

public final class ToolCapabilityOperatorPermissions {

    private ToolCapabilityOperatorPermissions() {
    }

    public static final OperatorPermission CAPABILITIES_READ =
            new OperatorPermission(
                    "operator.capabilities.read",
                    "View registered capabilities");

    public static final OperatorPermission CAPABILITY_INSPECT =
            new OperatorPermission(
                    "operator.capabilities.inspect",
                    "Inspect a registered capability");

    public static final OperatorPermission TOOLS_READ =
            new OperatorPermission(
                    "operator.tools.read",
                    "View registered tools");

    public static final OperatorPermission TOOL_INSPECT =
            new OperatorPermission(
                    "operator.tools.inspect",
                    "Inspect a registered tool");
}
