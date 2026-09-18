package tech.kayys.wayang.harness.contract.version;

import java.util.Objects;

/**
 * Semantic contract version indicating compatibility rules.
 * Major version changes signify incompatible contract changes.
 * Minor version changes signify backward-compatible additions.
 */
public record ContractVersion(int major, int minor) implements Comparable<ContractVersion> {

    public ContractVersion {
        if (major < 0 || minor < 0) {
            throw new IllegalArgumentException("Version components must be non-negative");
        }
    }

    public static ContractVersion of(int major, int minor) {
        return new ContractVersion(major, minor);
    }

    public static ContractVersion v1_0() {
        return new ContractVersion(1, 0);
    }

    public boolean isCompatibleWith(ContractVersion other) {
        if (other == null) return false;
        return this.major == other.major && this.minor >= other.minor;
    }

    @Override
    public int compareTo(ContractVersion o) {
        Objects.requireNonNull(o, "other");
        int comp = Integer.compare(this.major, o.major);
        if (comp != 0) return comp;
        return Integer.compare(this.minor, o.minor);
    }

    @Override
    public String toString() {
        return "v" + major + "." + minor;
    }
}
