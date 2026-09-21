package tech.kayys.andalus.spi.operator.sandbox;

import tech.kayys.andalus.spi.operator.OperatorPermission;

public final class SandboxOperatorPermissions {

    private SandboxOperatorPermissions() {
    }

    public static final OperatorPermission READ =
            new OperatorPermission(
                    "operator.sandboxes.read",
                    "View Andalus sandboxes");

    public static final OperatorPermission HEALTH =
            new OperatorPermission(
                    "operator.sandboxes.health",
                    "View sandbox health");

    public static final OperatorPermission METRICS =
            new OperatorPermission(
                    "operator.sandboxes.metrics",
                    "View sandbox metrics");

    public static final OperatorPermission DIAGNOSTICS =
            new OperatorPermission(
                    "operator.sandboxes.diagnostics",
                    "View sandbox diagnostics");

    public static final OperatorPermission STOP =
            new OperatorPermission(
                    "operator.sandboxes.stop",
                    "Stop a Andalus sandbox");

    public static final OperatorPermission DESTROY =
            new OperatorPermission(
                    "operator.sandboxes.destroy",
                    "Destroy a Andalus sandbox");
}
