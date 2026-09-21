package tech.kayys.andalus.execution.replay;

import java.util.Map;

/**
 * Sandboxes external effects during simulation, replay, and dry runs.
 */
public interface EffectSandbox {

    EffectDecision evaluate(String effectType, Map<String, Object> params);
}
