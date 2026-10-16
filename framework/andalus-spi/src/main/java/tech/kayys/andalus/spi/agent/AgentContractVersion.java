package tech.kayys.andalus.spi.agent;

import java.util.Objects;

/**
 * Semantic version of the Agent Contract.
 */
public record AgentContractVersion(
        int major,
        int minor,
        int patch
) implements Comparable<AgentContractVersion> {

    public AgentContractVersion {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException(
                    "Version components cannot be negative");
        }
    }

    public static AgentContractVersion of(
            int major,
            int minor,
            int patch) {

        return new AgentContractVersion(major, minor, patch);
    }

    public boolean compatibleWith(AgentContractVersion other) {
        Objects.requireNonNull(other, "other");

        return major == other.major;
    }

    @Override
    public int compareTo(AgentContractVersion other) {
        int result = Integer.compare(major, other.major);

        if (result != 0) {
            return result;
        }

        result = Integer.compare(minor, other.minor);

        if (result != 0) {
            return result;
        }

        return Integer.compare(patch, other.patch);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
