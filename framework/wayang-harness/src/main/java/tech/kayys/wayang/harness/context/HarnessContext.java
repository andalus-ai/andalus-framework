package tech.kayys.wayang.harness.context;

import java.util.Map;
import java.util.Optional;

/**
 * Runtime execution context carrying identity, session, and attributes.
 */
public interface HarnessContext {

    HarnessIdentity identity();

    HarnessSession session();

    Map<String, Object> attributes();

    Optional<Object> attribute(String key);

    HarnessContext withAttribute(String key, Object value);
}
