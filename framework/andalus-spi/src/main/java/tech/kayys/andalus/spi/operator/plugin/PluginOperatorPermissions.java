package tech.kayys.andalus.spi.operator.plugin;

import tech.kayys.andalus.spi.operator.OperatorPermission;

public final class PluginOperatorPermissions {

    private PluginOperatorPermissions() {
    }

    public static final OperatorPermission READ =
            new OperatorPermission(
                    "operator.plugins.read",
                    "View installed Andalus plugins");

    public static final OperatorPermission ENABLE =
            new OperatorPermission(
                    "operator.plugins.enable",
                    "Enable a Andalus plugin");

    public static final OperatorPermission DISABLE =
            new OperatorPermission(
                    "operator.plugins.disable",
                    "Disable a Andalus plugin");

    public static final OperatorPermission UNLOAD =
            new OperatorPermission(
                    "operator.plugins.unload",
                    "Unload a Andalus plugin");
}
