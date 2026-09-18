package tech.kayys.wayang.harness.controlplane;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;

import java.time.Instant;
import java.util.Map;

/**
 * Imperative command submitted to the Wayang control-plane asking the system to act.
 */
public interface WayangCommand {

    CommandId id();

    CommandType type();

    Instant timestamp();

    ExecutionId executionId();

    Map<String, Object> parameters();
}
