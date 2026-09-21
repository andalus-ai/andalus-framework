package tech.kayys.andalus.network.capability;

import java.util.List;

public record CapabilityRequirement(
        List<String> requiredCapabilities
) {

    public CapabilityRequirement {
        requiredCapabilities = requiredCapabilities == null ? List.of() : List.copyOf(requiredCapabilities);
    }

    public static CapabilityRequirement of(String... capabilities) {
        return new CapabilityRequirement(List.of(capabilities));
    }
}
