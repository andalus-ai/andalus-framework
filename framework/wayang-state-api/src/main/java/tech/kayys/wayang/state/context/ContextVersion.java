package tech.kayys.wayang.state.context;

public record ContextVersion(long sequence, String fingerprint) {
    public static ContextVersion initial() {
        return new ContextVersion(1L, "initial");
    }
}
