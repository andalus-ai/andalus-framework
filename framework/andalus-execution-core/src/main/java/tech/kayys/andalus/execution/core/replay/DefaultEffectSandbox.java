package tech.kayys.andalus.execution.core.replay;

import tech.kayys.andalus.execution.replay.EffectDecision;
import tech.kayys.andalus.execution.replay.EffectSandbox;

import java.util.Map;
import java.util.Set;

/**
 * Standard implementation of {@link EffectSandbox} suppressing external destructive side-effects during replay.
 */
public class DefaultEffectSandbox implements EffectSandbox {

    private static final Set<String> BLOCKED_TYPES = Set.of(
            "email", "send_email", "payment", "charge", "deploy", "deployment", "git_push", "delete_database"
    );

    @Override
    public EffectDecision evaluate(String effectType, Map<String, Object> params) {
        if (effectType == null) {
            return EffectDecision.block("Effect type cannot be null");
        }

        String lower = effectType.toLowerCase();
        if (BLOCKED_TYPES.contains(lower)) {
            return EffectDecision.block("Destructive external effect blocked during replay: " + effectType);
        }

        if (lower.contains("write") || lower.contains("file")) {
            return EffectDecision.virtualize("Filesystem write virtualized in replay sandbox");
        }

        return EffectDecision.record("Effect recorded/mocked");
    }
}
