package tech.kayys.wayang.spi.operator.diagnostics;

import tech.kayys.wayang.spi.operator.OperatorPermission;

public final class DiagnosticsOperatorPermissions {

    private DiagnosticsOperatorPermissions() {
    }

    public static final OperatorPermission READ =
            new OperatorPermission(
                    "operator.diagnostics.read",
                    "View runtime diagnostics");

    public static final OperatorPermission INSPECT =
            new OperatorPermission(
                    "operator.diagnostics.inspect",
                    "Inspect a component diagnostic");

    public static final OperatorPermission PLATFORM =
            new OperatorPermission(
                    "operator.diagnostics.platform",
                    "Run platform diagnostics");
}
