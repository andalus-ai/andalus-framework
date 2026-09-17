package tech.kayys.wayang.harness.environment;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory thread-safe implementation of {@link HarnessCapabilities}.
 */
public class DefaultHarnessCapabilities implements HarnessCapabilities {

    private final Set<CapabilityId> capabilities = ConcurrentHashMap.newKeySet();

    public DefaultHarnessCapabilities() {}

    public DefaultHarnessCapabilities(Set<CapabilityId> initial) {
        if (initial != null) {
            capabilities.addAll(initial);
        }
    }

    public static DefaultHarnessCapabilities of(CapabilityId... caps) {
        return new DefaultHarnessCapabilities(Set.of(caps));
    }

    public void register(CapabilityId capability) {
        Objects.requireNonNull(capability, "capability");
        capabilities.add(capability);
    }

    @Override
    public boolean has(CapabilityId capability) {
        Objects.requireNonNull(capability, "capability");
        return capabilities.contains(capability);
    }

    @Override
    public void require(CapabilityId capability) {
        if (!has(capability)) {
            throw new SecurityException("Required capability not available: " + capability.value());
        }
    }

    @Override
    public Set<CapabilityId> available() {
        return Set.copyOf(capabilities);
    }
}
