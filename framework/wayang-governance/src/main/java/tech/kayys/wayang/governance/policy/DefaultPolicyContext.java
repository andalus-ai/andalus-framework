package tech.kayys.wayang.governance.policy;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Default immutable implementation of {@link PolicyContext}.
 */
public record DefaultPolicyContext(
        Object identity,
        Object session,
        Object environment,
        Optional<Object> workspace,
        Map<String, Object> attributes
) implements PolicyContext {

    public DefaultPolicyContext {
        Objects.requireNonNull(identity, "identity");
        Objects.requireNonNull(session, "session");
        Objects.requireNonNull(environment, "environment");
        workspace = workspace == null ? Optional.empty() : workspace;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static DefaultPolicyContext of(Object identity, Object session, Object environment, Object workspace, Map<String, Object> attributes) {
        return new DefaultPolicyContext(identity, session, environment, Optional.ofNullable(workspace), attributes);
    }
}
