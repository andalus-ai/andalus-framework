package tech.kayys.wayang.harness.environment.v3.network;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * Request to establish a network connection or access an endpoint.
 */
public record NetworkRequest(
        String host,
        int port,
        String protocol,
        Duration timeout,
        Map<String, String> headers
) {
    public NetworkRequest {
        Objects.requireNonNull(host, "host");
        protocol = protocol != null ? protocol : "https";
        timeout = timeout != null ? timeout : Duration.ofSeconds(30);
        headers = headers != null ? Map.copyOf(headers) : Map.of();
    }

    public static NetworkRequest of(String host, int port) {
        return new NetworkRequest(host, port, "https", Duration.ofSeconds(30), Map.of());
    }
}
