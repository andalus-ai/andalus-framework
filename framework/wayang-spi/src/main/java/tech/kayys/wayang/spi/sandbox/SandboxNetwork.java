package tech.kayys.wayang.spi.sandbox;

import java.util.List;
import java.util.Objects;

public interface SandboxNetwork {

    NetworkMode mode();

    boolean allows(NetworkRule rule);

    void validate(NetworkRule rule);

    default List<NetworkRule> rules() {
        return List.of();
    }

    static SandboxNetwork disabled() {
        return new SandboxNetwork() {
            @Override
            public NetworkMode mode() {
                return NetworkMode.DISABLED;
            }

            @Override
            public boolean allows(NetworkRule rule) {
                return false;
            }

            @Override
            public void validate(NetworkRule rule) {
                throw new IllegalStateException("Network access is disabled: " + rule);
            }
        };
    }

    static SandboxNetwork full() {
        return new SandboxNetwork() {
            @Override
            public NetworkMode mode() {
                return NetworkMode.FULL;
            }

            @Override
            public boolean allows(NetworkRule rule) {
                return true;
            }

            @Override
            public void validate(NetworkRule rule) {
                // Allowed
            }
        };
    }

    static SandboxNetwork restricted(List<NetworkRule> allowRules) {
        final List<NetworkRule> rules = allowRules != null ? List.copyOf(allowRules) : List.of();
        return new SandboxNetwork() {
            @Override
            public NetworkMode mode() {
                return NetworkMode.RESTRICTED;
            }

            @Override
            public List<NetworkRule> rules() {
                return rules;
            }

            @Override
            public boolean allows(NetworkRule rule) {
                Objects.requireNonNull(rule, "rule");
                return rules.stream().anyMatch(r ->
                        r.protocol() == rule.protocol()
                        && Objects.equals(r.port(), rule.port())
                        && r.host().equalsIgnoreCase(rule.host()));
            }

            @Override
            public void validate(NetworkRule rule) {
                if (!allows(rule)) {
                    throw new IllegalStateException("Network access denied: " + rule);
                }
            }
        };
    }
}
