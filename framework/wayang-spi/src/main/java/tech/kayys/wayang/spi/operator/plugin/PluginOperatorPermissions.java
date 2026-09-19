package tech.kayys.wayang.spi.operator.plugin;

import tech.kayys.wayang.spi.operator.OperatorPermission;

public final class PluginOperatorPermissions {

    private PluginOperatorPermissions() {
    }

    public static final OperatorPermission READ =
            new OperatorPermission(
                    "operator.plugins.read",
                    "View installed Wayang plugins");

    public static final OperatorPermission ENABLE =
            new OperatorPermission(
                    "operator.plugins.enable",
                    "Enable a Wayang plugin");

    public static final OperatorPermission DISABLE =
            new OperatorPermission(
                    "operator.plugins.disable",
                    "Disable a Wayang plugin");

    public static final OperatorPermission UNLOAD =
            new OperatorPermission(
                    "operator.plugins.unload",
                    "Unload a Wayang plugin");
}
