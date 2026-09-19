package tech.kayys.wayang.state;

import java.util.Objects;

/**
 * Immutable key identifying a state slice or component.
 */
public record StateKey(String scope, String name) {
    public StateKey {
        Objects.requireNonNull(scope, "scope cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
    }

    public static StateKey of(String scope, String name) {
        return new StateKey(scope, name);
    }

    public static StateKey of(String name) {
        return new StateKey("default", name);
    }
}
