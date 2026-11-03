package tech.kayys.andalus.harness.fabric;

import java.time.Duration;

/** Allows a running handler to extend its current queue claim. */
@FunctionalInterface
public interface ClaimVisibility {
    /**
     * Extends visibility for the exact claim generation.
     *
     * @return false when the claim expired, was replaced, or is no longer owned
     */
    boolean extend(Duration extension);
}
