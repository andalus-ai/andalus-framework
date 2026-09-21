package tech.kayys.andalus.execution.conformance;

import java.util.Objects;

/**
 * Stable, unambiguous identifier for a formal execution contract.
 */
public record ContractId(String namespace, String name, String version) {

    public ContractId {
        Objects.requireNonNull(namespace, "namespace cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(version, "version cannot be null");
    }

    public static ContractId of(String namespace, String name, String version) {
        return new ContractId(namespace, name, version);
    }

    public static ContractId of(String qualifiedName) {
        Objects.requireNonNull(qualifiedName, "qualifiedName cannot be null");
        String[] parts = qualifiedName.split(":");
        if (parts.length >= 3) {
            return new ContractId(parts[0], parts[1], parts[2]);
        } else if (parts.length == 2) {
            return new ContractId(parts[0], parts[1], "1.0.0");
        } else {
            return new ContractId("andalus", qualifiedName, "1.0.0");
        }
    }

    public String canonicalName() {
        return namespace + ":" + name + ":" + version;
    }
}
