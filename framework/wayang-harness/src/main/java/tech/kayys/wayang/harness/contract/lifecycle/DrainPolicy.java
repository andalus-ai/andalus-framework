package tech.kayys.wayang.harness.contract.lifecycle;

import java.time.Duration;

/**
 * Policy governing behavior when an agent enters DRAINING state.
 */
public record DrainPolicy(
        boolean allowActiveTaskCompletion,
        Duration maxDrainTimeout
) {
    public static DrainPolicy defaultPolicy() {
        return new DrainPolicy(true, Duration.ofMinutes(5));
    }

    public static DrainPolicy immediate() {
        return new DrainPolicy(false, Duration.ZERO);
    }
}
