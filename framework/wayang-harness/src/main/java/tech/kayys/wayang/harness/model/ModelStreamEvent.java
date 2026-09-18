package tech.kayys.wayang.harness.model;

public record ModelStreamEvent(
        String type,
        Object payload
) {
    public static ModelStreamEvent token(String token) {
        return new ModelStreamEvent("TOKEN", token);
    }

    public static ModelStreamEvent reasoning(String delta) {
        return new ModelStreamEvent("REASONING_DELTA", delta);
    }
}
