package tech.kayys.wayang.execution.replay;

import java.util.Objects;

/**
 * Sandboxing decision for a side effect requested during replay.
 */
public record EffectDecision(
        EffectMode mode,
        boolean allowed,
        String reason
) {
    public EffectDecision {
        Objects.requireNonNull(mode, "mode cannot be null");
        reason = reason == null ? "" : reason;
    }

    public static EffectDecision block(String reason) {
        return new EffectDecision(EffectMode.BLOCKED, false, reason);
    }

    public static EffectDecision virtualize(String reason) {
        return new EffectDecision(EffectMode.VIRTUAL, true, reason);
    }

    public static EffectDecision record(String reason) {
        return new EffectDecision(EffectMode.RECORDED, true, reason);
    }

    public static EffectDecision real() {
        return new EffectDecision(EffectMode.REAL, true, "Real effect permitted");
    }
}
