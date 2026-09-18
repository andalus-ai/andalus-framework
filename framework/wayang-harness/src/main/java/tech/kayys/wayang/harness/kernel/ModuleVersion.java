package tech.kayys.wayang.harness.kernel;

/**
 * Semantic version of a Wayang runtime module.
 */
public record ModuleVersion(int major, int minor, int patch) implements Comparable<ModuleVersion> {

    public static ModuleVersion of(int major, int minor, int patch) {
        return new ModuleVersion(major, minor, patch);
    }

    public static ModuleVersion initial() {
        return new ModuleVersion(1, 0, 0);
    }

    public boolean isCompatibleWith(ModuleVersion other) {
        if (other == null) {
            return false;
        }
        // Major versions must match; minor must be greater than or equal
        return this.major == other.major && this.minor >= other.minor;
    }

    @Override
    public int compareTo(ModuleVersion o) {
        if (this.major != o.major) {
            return Integer.compare(this.major, o.major);
        }
        if (this.minor != o.minor) {
            return Integer.compare(this.minor, o.minor);
        }
        return Integer.compare(this.patch, o.patch);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
