package tech.kayys.wayang.harness.kernel;

import java.util.Objects;

/**
 * Declares a dependency required by a module.
 */
public record ModuleDependency(ModuleId id, ModuleVersion minVersion, boolean optional) {

    public ModuleDependency {
        Objects.requireNonNull(id, "ModuleId cannot be null");
        minVersion = minVersion != null ? minVersion : ModuleVersion.initial();
    }

    public static ModuleDependency required(ModuleId id) {
        return new ModuleDependency(id, ModuleVersion.initial(), false);
    }

    public static ModuleDependency required(ModuleId id, ModuleVersion minVersion) {
        return new ModuleDependency(id, minVersion, false);
    }

    public static ModuleDependency optional(ModuleId id) {
        return new ModuleDependency(id, ModuleVersion.initial(), true);
    }
}
