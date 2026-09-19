package tech.kayys.wayang.execution.network;

import java.util.Objects;

/**
 * Firewall / routing rule within a network policy.
 */
public record NetworkRule(
        String hostOrCidr,
        int port,
        String protocol,
        boolean allow
) {

    public NetworkRule {
        Objects.requireNonNull(hostOrCidr, "hostOrCidr cannot be null");
        hostOrCidr = hostOrCidr.trim();
        protocol = protocol != null ? protocol.trim().toUpperCase() : "TCP";
    }

    public static NetworkRule allowHost(String host, int port) {
        return new NetworkRule(host, port, "TCP", true);
    }

    public static NetworkRule allowHost(String host) {
        return new NetworkRule(host, 443, "TCP", true);
    }

    public static NetworkRule denyAll() {
        return new NetworkRule("*", 0, "*", false);
    }
}
