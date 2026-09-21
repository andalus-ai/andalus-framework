package tech.kayys.andalus.harness.controlplane;

import tech.kayys.andalus.harness.consistency.state.ExecutionId;

import java.time.Instant;
import java.util.Map;

/**
 * Imperative command submitted to the Andalus control-plane asking the system to act.
 */
public interface AndalusCommand {

    CommandId id();

    CommandType type();

    Instant timestamp();

    ExecutionId executionId();

    Map<String, Object> parameters();
}
