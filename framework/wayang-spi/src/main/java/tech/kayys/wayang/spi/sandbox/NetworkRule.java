package tech.kayys.wayang.spi.sandbox;

public record NetworkRule(
        NetworkProtocol protocol,
        String host,
        Integer port
) {

    public NetworkRule {
        if (protocol == null) {
            throw new IllegalArgumentException(
                    "protocol cannot be null"
            );
        }

        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException(
                    "host cannot be null or blank"
            );
        }

        if (port != null &&
                (port < 1 || port > 65535)) {

            throw new IllegalArgumentException(
                    "port must be between 1 and 65535"
            );
        }

        host = host.trim();
    }
}
