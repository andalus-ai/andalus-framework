package tech.kayys.wayang.harness.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Standard thread-safe implementation of {@link HarnessContext}.
 */
public class DefaultHarnessContext implements HarnessContext {

    private final HarnessIdentity identity;
    private final HarnessSession session;
    private final Map<String, Object> attributes;

    public DefaultHarnessContext(HarnessIdentity identity, HarnessSession session) {
        this(identity, session, Map.of());
    }

    public DefaultHarnessContext(HarnessIdentity identity, HarnessSession session, Map<String, Object> attributes) {
        this.identity = Objects.requireNonNull(identity, "identity");
        this.session = Objects.requireNonNull(session, "session");
        this.attributes = Map.copyOf(attributes != null ? attributes : Map.of());
    }

    @Override
    public HarnessIdentity identity() {
        return identity;
    }

    @Override
    public HarnessSession session() {
        return session;
    }

    @Override
    public Map<String, Object> attributes() {
        return attributes;
    }

    @Override
    public Optional<Object> attribute(String key) {
        return Optional.ofNullable(attributes.get(key));
    }

    @Override
    public HarnessContext withAttribute(String key, Object value) {
        Map<String, Object> copy = new HashMap<>(this.attributes);
        if (value != null) {
            copy.put(key, value);
        } else {
            copy.remove(key);
        }
        return new DefaultHarnessContext(this.identity, this.session, copy);
    }
}
