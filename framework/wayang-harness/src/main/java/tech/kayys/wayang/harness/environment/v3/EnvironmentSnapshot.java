package tech.kayys.wayang.harness.environment.v3;

import tech.kayys.wayang.harness.environment.v3.resource.ResourceDescriptor;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable snapshot of an environment substrate for reproducibility.
 */
public record EnvironmentSnapshot(
        String id,
        EnvironmentDescriptor descriptor,
        Collection<ResourceDescriptor> resources,
        Instant createdAt
) {
    public EnvironmentSnapshot {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(descriptor, "descriptor");
        resources = resources != null ? List.copyOf(resources) : List.of();
        createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static EnvironmentSnapshot of(WayangEnvironment env) {
        Objects.requireNonNull(env, "env");
        return new EnvironmentSnapshot(
                "snap-env-" + UUID.randomUUID(),
                env.descriptor(),
                env.resources().available(),
                Instant.now()
        );
    }
}
