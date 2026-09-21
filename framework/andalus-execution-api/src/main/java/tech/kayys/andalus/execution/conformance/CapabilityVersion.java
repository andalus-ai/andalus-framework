package tech.kayys.andalus.execution.conformance;

/**
 * SemVer representation for capability contracts.
 */
public record CapabilityVersion(int major, int minor, int patch) implements Comparable<CapabilityVersion> {

    public static CapabilityVersion of(int major, int minor, int patch) {
        return new CapabilityVersion(major, minor, patch);
    }

    public static CapabilityVersion parse(String version) {
        if (version == null || version.isBlank()) {
            return new CapabilityVersion(1, 0, 0);
        }
        String[] parts = version.split("\\.");
        int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 1;
        int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        return new CapabilityVersion(major, minor, patch);
    }

    public boolean isCompatibleWith(CapabilityVersion required) {
        return this.major == required.major && (this.minor > required.minor || (this.minor == required.minor && this.patch >= required.patch));
    }

    @Override
    public int compareTo(CapabilityVersion o) {
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
