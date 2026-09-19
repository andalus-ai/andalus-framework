package tech.kayys.wayang.spi.operator.configuration;

import tech.kayys.wayang.spi.operator.OperatorPermission;

/**
 * Granular permissions for the Configuration Operator service.
 */
public final class ConfigurationOperatorPermissions {

    private ConfigurationOperatorPermissions() {
    }

    public static final OperatorPermission READ =
            new OperatorPermission(
                    "operator.configuration.read",
                    "Read configuration metadata and keys");

    public static final OperatorPermission INSPECT =
            new OperatorPermission(
                    "operator.configuration.inspect",
                    "Inspect configuration values (sensitive values masked)");

    public static final OperatorPermission UPDATE =
            new OperatorPermission(
                    "operator.configuration.update",
                    "Update dynamic runtime or tenant configuration");

    public static final OperatorPermission ACTIVATE =
            new OperatorPermission(
                    "operator.configuration.activate",
                    "Activate a configuration version");
}
