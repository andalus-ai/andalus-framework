package tech.kayys.wayang.spi.sandbox.container;

import java.util.Map;
import java.util.Set;

public record ContainerSecurityProfile(
        boolean privileged,
        boolean readOnlyRoot,
        boolean noNewPrivileges,
        Set<String> droppedCapabilities,
        Set<String> addedCapabilities,
        Map<String, Object> attributes
) {

    public ContainerSecurityProfile {
        droppedCapabilities =
                droppedCapabilities == null
                        ? Set.of()
                        : Set.copyOf(droppedCapabilities);

        addedCapabilities =
                addedCapabilities == null
                        ? Set.of()
                        : Set.copyOf(addedCapabilities);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public static ContainerSecurityProfile hardened() {
        return new ContainerSecurityProfile(
                false,
                true,
                true,
                Set.of("ALL"),
                Set.of(),
                Map.of()
        );
    }
}
