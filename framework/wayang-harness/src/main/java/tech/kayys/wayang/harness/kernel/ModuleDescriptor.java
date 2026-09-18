package tech.kayys.wayang.harness.kernel;

import java.util.Objects;
import java.util.Set;

/**
 * Metadata and dependency descriptor for a Wayang module.
 */
public record ModuleDescriptor(
        ModuleId id,
        ModuleVersion version,
        String description,
        Set<ModuleDependency> dependencies
) {

    public ModuleDescriptor {
        Objects.requireNonNull(id, "ModuleId cannot be null");
        Objects.requireNonNull(version, "ModuleVersion cannot be null");
        description = description != null ? description : "";
        dependencies = dependencies != null ? Set.copyOf(dependencies) : Set.of();
    }

    public static ModuleDescriptor of(ModuleId id, ModuleVersion version, String description, Set<ModuleDependency> dependencies) {
        return new ModuleDescriptor(id, version, description, dependencies);
    }

    public static ModuleDescriptor of(ModuleId id, ModuleVersion version) {
        return new ModuleDescriptor(id, version, "", Set.of());
    }
}
