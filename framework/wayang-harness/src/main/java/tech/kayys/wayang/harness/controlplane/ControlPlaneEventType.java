package tech.kayys.wayang.harness.controlplane;

import java.util.Objects;

/**
 * Standard classification of control-plane events.
 */
public record ControlPlaneEventType(String name) {

    public ControlPlaneEventType {
        Objects.requireNonNull(name, "ControlPlaneEventType name cannot be null");
    }

    public static ControlPlaneEventType of(String name) {
        return new ControlPlaneEventType(name);
    }

    public static final ControlPlaneEventType EXECUTION_STARTED = of("EXECUTION_STARTED");
    public static final ControlPlaneEventType NODE_ASSIGNED = of("NODE_ASSIGNED");
    public static final ControlPlaneEventType NODE_COMPLETED = of("NODE_COMPLETED");
    public static final ControlPlaneEventType EXECUTION_CANCELLED = of("EXECUTION_CANCELLED");
    public static final ControlPlaneEventType WORKER_DRAINED = of("WORKER_DRAINED");
    public static final ControlPlaneEventType APPROVAL_GRANTED = of("APPROVAL_GRANTED");
}
