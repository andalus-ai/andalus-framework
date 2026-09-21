package tech.kayys.andalus.execution.conformance;

/**
 * SemVer representation for contracts.
 */
public record ContractVersion(int major, int minor, int patch) {

    public static ContractVersion of(int major, int minor, int patch) {
        return new ContractVersion(major, minor, patch);
    }

    public static ContractVersion parse(String version) {
        if (version == null || version.isBlank()) {
            return new ContractVersion(1, 0, 0);
        }
        String[] parts = version.split("\\.");
        int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 1;
        int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        return new ContractVersion(major, minor, patch);
    }

    public boolean isCompatibleWith(ContractVersion required) {
        return this.major == required.major && (this.minor > required.minor || (this.minor == required.minor && this.patch >= required.patch));
    }
}
