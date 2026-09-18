package tech.kayys.wayang.harness.protocol;

import java.util.Set;

public interface CapabilityView {

    Set<String> permittedCapabilities();

    boolean isPermitted(String capability);

    static CapabilityView of(Set<String> capabilities) {
        Set<String> caps = capabilities != null ? Set.copyOf(capabilities) : Set.of();
        return new CapabilityView() {
            @Override
            public Set<String> permittedCapabilities() {
                return caps;
            }

            @Override
            public boolean isPermitted(String capability) {
                return caps.contains("*") || caps.contains(capability);
            }
        };
    }
}
