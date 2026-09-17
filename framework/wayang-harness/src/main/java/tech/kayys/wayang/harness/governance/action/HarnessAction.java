package tech.kayys.wayang.harness.governance.action;

import tech.kayys.wayang.harness.environment.ResourceId;

import java.util.Map;
import java.util.Optional;

/**
 * Normalized representation of an agent action submitted to the governance boundary.
 */
public interface HarnessAction {

    String id();

    String type();

    String capability();

    Optional<ResourceId> resource();

    Map<String, Object> arguments();

    ActionMetadata metadata();
}
