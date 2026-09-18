package tech.kayys.wayang.governance.policy;

import java.util.Map;
import java.util.Optional;

/**
 * Contextual state provided to policies during evaluation.
 */
public interface PolicyContext {

    Object identity();

    default <T> Optional<T> identity(Class<T> type) {
        return Optional.ofNullable(identity()).filter(type::isInstance).map(type::cast);
    }

    Object session();

    default <T> Optional<T> session(Class<T> type) {
        return Optional.ofNullable(session()).filter(type::isInstance).map(type::cast);
    }

    Object environment();

    default <T> Optional<T> environment(Class<T> type) {
        return Optional.ofNullable(environment()).filter(type::isInstance).map(type::cast);
    }

    Optional<?> workspace();

    default <T> Optional<T> workspace(Class<T> type) {
        return workspace().filter(type::isInstance).map(type::cast);
    }

    Map<String, Object> attributes();
}
