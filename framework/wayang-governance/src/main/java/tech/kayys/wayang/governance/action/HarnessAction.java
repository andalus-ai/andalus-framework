package tech.kayys.wayang.governance.action;

import java.util.Map;
import java.util.Optional;

/**
 * Normalized representation of an agent action submitted to the governance boundary.
 */
public interface HarnessAction {

    String id();

    String type();

    String capability();

    Optional<?> resource();

    default <T> Optional<T> resource(Class<T> type) {
        return resource().filter(type::isInstance).map(type::cast);
    }

    Map<String, Object> arguments();

    ActionMetadata metadata();
}
