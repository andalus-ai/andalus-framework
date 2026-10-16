package tech.kayys.andalus.spi.agent;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Stable identity of an agent.
 *
 * <p>The identity is deliberately independent from deployment location,
 * tenant, process, executor or runtime implementation.</p>
 */
public record AgentIdentity(
        String id,
        String name,
        String namespace
) {

    private static final Pattern ID_PATTERN =
            Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._:-]*");

    public AgentIdentity {
        Objects.requireNonNull(id, "id cannot be null");

        if (id.isBlank()) {
            throw new IllegalArgumentException("id cannot be blank");
        }

        if (!ID_PATTERN.matcher(id).matches()) {
            throw new IllegalArgumentException(
                    "Invalid agent id: " + id);
        }

        name = name == null || name.isBlank()
                ? id
                : name;

        namespace = namespace == null || namespace.isBlank()
                ? "default"
                : namespace;
    }

    public static AgentIdentity of(String id) {
        return new AgentIdentity(id, id, "default");
    }

    public static AgentIdentity of(
            String namespace,
            String id,
            String name) {

        return new AgentIdentity(id, name, namespace);
    }
}
