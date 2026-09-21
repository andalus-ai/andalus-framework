package tech.kayys.andalus.execution.portability;

import tech.kayys.andalus.execution.replay.ReplaySafety;
import java.util.Objects;

/**
 * Declared replayability constraints for portable executions.
 */
public record Replayability(
    boolean isDeterministic,
    ReplaySafety safety,
    String clockType
) {
    public Replayability {
        Objects.requireNonNull(safety, "safety cannot be null");
        Objects.requireNonNull(clockType, "clockType cannot be null");
    }

    public static Replayability ofDeterministic() {
        return new Replayability(true, ReplaySafety.SAFE, "FIXED");
    }

    public static Replayability ofStandard() {
        return new Replayability(false, ReplaySafety.READ_ONLY, "SYSTEM");
    }
}
