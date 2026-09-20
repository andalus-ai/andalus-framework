package tech.kayys.wayang.execution.conformance;

import java.util.Objects;

/**
 * Stable, normalized identifier for an execution capability.
 */
public record CapabilityId(String namespace, String name) {

    public CapabilityId {
        Objects.requireNonNull(namespace, "namespace cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
    }

    public static CapabilityId of(String namespace, String name) {
        return new CapabilityId(namespace, name);
    }

    public static CapabilityId of(String qualifiedName) {
        Objects.requireNonNull(qualifiedName, "qualifiedName cannot be null");
        int separator = qualifiedName.indexOf(':');
        if (separator != -1) {
            return new CapabilityId(qualifiedName.substring(0, separator), qualifiedName.substring(separator + 1));
        }
        int lastDot = qualifiedName.lastIndexOf('.');
        if (lastDot != -1) {
            return new CapabilityId(qualifiedName.substring(0, lastDot), qualifiedName.substring(lastDot + 1));
        }
        return new CapabilityId("wayang", qualifiedName);
    }

    public String qualifiedName() {
        return namespace + ":" + name;
    }
}
