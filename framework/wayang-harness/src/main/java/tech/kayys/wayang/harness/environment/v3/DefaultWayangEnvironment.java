package tech.kayys.wayang.harness.environment.v3;

import tech.kayys.wayang.harness.capability.CapabilityScope;
import tech.kayys.wayang.harness.capability.DefaultCapabilityScope;
import tech.kayys.wayang.harness.environment.v3.resource.DefaultResourceCatalog;
import tech.kayys.wayang.harness.environment.v3.resource.ResourceCatalog;

import java.util.Objects;

/**
 * Default implementation of {@link WayangEnvironment}.
 */
public class DefaultWayangEnvironment implements WayangEnvironment {

    private final EnvironmentId id;
    private final EnvironmentDescriptor descriptor;
    private final ResourceCatalog resources;
    private final CapabilityScope capabilities;
    private volatile EnvironmentState state;

    public DefaultWayangEnvironment(
            EnvironmentId id,
            EnvironmentDescriptor descriptor,
            ResourceCatalog resources,
            CapabilityScope capabilities,
            EnvironmentState state
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.descriptor = Objects.requireNonNull(descriptor, "descriptor");
        this.resources = resources != null ? resources : new DefaultResourceCatalog();
        this.capabilities = capabilities != null ? capabilities : DefaultCapabilityScope.allowAll();
        this.state = state != null ? state : EnvironmentState.READY;
    }

    public static DefaultWayangEnvironment local() {
        EnvironmentId id = EnvironmentId.generate();
        return new DefaultWayangEnvironment(
                id,
                EnvironmentDescriptor.localDefault(id),
                new DefaultResourceCatalog(),
                DefaultCapabilityScope.allowAll(),
                EnvironmentState.READY
        );
    }

    @Override
    public EnvironmentId id() {
        return id;
    }

    @Override
    public EnvironmentDescriptor descriptor() {
        return descriptor;
    }

    @Override
    public ResourceCatalog resources() {
        return resources;
    }

    @Override
    public CapabilityScope capabilities() {
        return capabilities;
    }

    @Override
    public EnvironmentState state() {
        return state;
    }

    public void setState(EnvironmentState state) {
        this.state = Objects.requireNonNull(state, "state");
    }
}
