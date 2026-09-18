package tech.kayys.wayang.spi.sandbox;

public final class SandboxStateMachine {

    private SandboxStateMachine() {
    }

    public static boolean canTransition(SandboxState from, SandboxState to) {
        if (from == null || to == null) {
            return false;
        }

        return switch (from) {
            case CREATED ->
                    to == SandboxState.STARTING
                            || to == SandboxState.DESTROYED;

            case STARTING ->
                    to == SandboxState.RUNNING
                            || to == SandboxState.FAILED
                            || to == SandboxState.STOPPING;

            case RUNNING ->
                    to == SandboxState.STOPPING
                            || to == SandboxState.FAILED;

            case STOPPING ->
                    to == SandboxState.STOPPED
                            || to == SandboxState.FAILED;

            case STOPPED ->
                    to == SandboxState.STARTING
                            || to == SandboxState.DESTROYED;

            case FAILED ->
                    to == SandboxState.STARTING
                            || to == SandboxState.DESTROYED;

            case DESTROYED ->
                    false;
        };
    }

    public static void requireTransition(SandboxState from, SandboxState to) {
        if (!canTransition(from, to)) {
            throw new IllegalStateException("Illegal sandbox state transition: " + from + " -> " + to);
        }
    }
}
