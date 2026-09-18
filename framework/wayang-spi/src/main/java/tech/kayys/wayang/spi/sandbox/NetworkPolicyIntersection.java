package tech.kayys.wayang.spi.sandbox;

public final class NetworkPolicyIntersection {

    private NetworkPolicyIntersection() {
    }

    public static NetworkMode mode(NetworkMode left, NetworkMode right) {
        if (left == NetworkMode.DISABLED || right == NetworkMode.DISABLED) {
            return NetworkMode.DISABLED;
        }

        if (left == NetworkMode.RESTRICTED || right == NetworkMode.RESTRICTED) {
            return NetworkMode.RESTRICTED;
        }

        return NetworkMode.FULL;
    }
}
