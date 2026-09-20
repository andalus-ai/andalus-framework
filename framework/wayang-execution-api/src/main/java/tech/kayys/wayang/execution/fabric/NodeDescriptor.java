package tech.kayys.wayang.execution.fabric;

import tech.kayys.wayang.execution.conformance.ConformanceProfile;
import tech.kayys.wayang.execution.portability.RuntimeIdentity;

import java.util.Objects;

/**
 * Static description and capabilities of an execution node.
 */
public record NodeDescriptor(
    NodeIdentity identity,
    RuntimeIdentity runtime,
    ConformanceProfile profile,
    TopologyDescriptor topology
) {
    public NodeDescriptor {
        Objects.requireNonNull(identity, "identity cannot be null");
        Objects.requireNonNull(runtime, "runtime cannot be null");
        Objects.requireNonNull(profile, "profile cannot be null");
        Objects.requireNonNull(topology, "topology cannot be null");
    }
}
