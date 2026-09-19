package tech.kayys.wayang.spi.operator.session;

import tech.kayys.wayang.spi.operator.OperatorPermission;

public final class SessionOperatorPermissions {

    private SessionOperatorPermissions() {
    }

    public static final OperatorPermission READ =
            new OperatorPermission(
                    "operator.sessions.read",
                    "View sessions");

    public static final OperatorPermission INSPECT =
            new OperatorPermission(
                    "operator.sessions.inspect",
                    "Inspect a session");

    public static final OperatorPermission SUSPEND =
            new OperatorPermission(
                    "operator.sessions.suspend",
                    "Suspend a session");

    public static final OperatorPermission RESUME =
            new OperatorPermission(
                    "operator.sessions.resume",
                    "Resume a session");

    public static final OperatorPermission CLOSE =
            new OperatorPermission(
                    "operator.sessions.close",
                    "Close a session");
}
