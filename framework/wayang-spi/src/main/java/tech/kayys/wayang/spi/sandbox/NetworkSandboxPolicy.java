package tech.kayys.wayang.spi.sandbox;

import java.util.List;
import java.util.Map;

public record NetworkSandboxPolicy(
        NetworkMode mode,
        List<NetworkRule> allow,
        List<NetworkRule> deny,
        boolean allowDns,
        boolean allowLoopback,
        Map<String, Object> attributes
) {

    public NetworkSandboxPolicy {
        mode = mode == null
                ? NetworkMode.DISABLED
                : mode;

        allow = allow == null
                ? List.of()
                : List.copyOf(allow);

        deny = deny == null
                ? List.of()
                : List.copyOf(deny);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public static NetworkSandboxPolicy disabled() {
        return new NetworkSandboxPolicy(
                NetworkMode.DISABLED,
                List.of(),
                List.of(),
                false,
                false,
                Map.of()
        );
    }

    public static NetworkSandboxPolicy full() {
        return new NetworkSandboxPolicy(
                NetworkMode.FULL,
                List.of(),
                List.of(),
                true,
                true,
                Map.of()
        );
    }

    public static NetworkSandboxPolicy restricted(
            List<NetworkRule> allow) {

        return new NetworkSandboxPolicy(
                NetworkMode.RESTRICTED,
                allow,
                List.of(),
                false,
                false,
                Map.of()
        );
    }
}
