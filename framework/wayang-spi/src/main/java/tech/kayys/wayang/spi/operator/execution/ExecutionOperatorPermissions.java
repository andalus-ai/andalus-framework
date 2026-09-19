package tech.kayys.wayang.spi.operator.execution;

import tech.kayys.wayang.spi.operator.OperatorPermission;

public final class ExecutionOperatorPermissions {

    private ExecutionOperatorPermissions() {
    }

    public static final OperatorPermission READ =
            new OperatorPermission(
                    "operator.executions.read",
                    "View executions");

    public static final OperatorPermission INSPECT =
            new OperatorPermission(
                    "operator.executions.inspect",
                    "Inspect an execution");

    public static final OperatorPermission PAUSE =
            new OperatorPermission(
                    "operator.executions.pause",
                    "Pause an execution");

    public static final OperatorPermission RESUME =
            new OperatorPermission(
                    "operator.executions.resume",
                    "Resume an execution");

    public static final OperatorPermission CANCEL =
            new OperatorPermission(
                    "operator.executions.cancel",
                    "Cancel an execution");

    public static final OperatorPermission RETRY =
            new OperatorPermission(
                    "operator.executions.retry",
                    "Retry an execution");
}
