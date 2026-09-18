package tech.kayys.wayang.spi.sandbox;

import java.util.List;

public record SandboxNetwork(
        NetworkMode mode,
        List<NetworkRule> rules
) {

    public SandboxNetwork {
        if (mode == null) {
            throw new IllegalArgumentException(
                    "mode cannot be null"
            );
        }

        rules = rules == null
                ? List.of()
                : List.copyOf(rules);
    }

    public static SandboxNetwork disabled() {
        return new SandboxNetwork(
                NetworkMode.DISABLED,
                List.of()
        );
    }

    public static SandboxNetwork full() {
        return new SandboxNetwork(
                NetworkMode.FULL,
                List.of()
        );
    }

    public static SandboxNetwork restricted(List<NetworkRule> rules) {
        return new SandboxNetwork(
                NetworkMode.RESTRICTED,
                rules
        );
    }
}
