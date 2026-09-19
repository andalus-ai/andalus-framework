package tech.kayys.wayang.execution.sandbox;

import java.time.Duration;
import java.util.List;

/**
 * Sandbox lifecycle parameters and event hooks.
 */
public record SandboxLifecycle(
        Duration startupTimeout,
        Duration shutdownTimeout,
        boolean autoDestroyOnCompletion,
        List<SandboxLifecycleHook> hooks
) {

    public SandboxLifecycle {
        startupTimeout = startupTimeout != null ? startupTimeout : Duration.ofSeconds(30);
        shutdownTimeout = shutdownTimeout != null ? shutdownTimeout : Duration.ofSeconds(30);
        hooks = hooks != null ? List.copyOf(hooks) : List.of();
    }

    public static SandboxLifecycle standard() {
        return new SandboxLifecycle(Duration.ofSeconds(30), Duration.ofSeconds(30), true, List.of());
    }
}
