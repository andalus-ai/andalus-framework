package tech.kayys.wayang.harness.model;

public record GenerationParameters(
        Double temperature,
        Integer maxTokens,
        Double topP,
        Long seed,
        StopConditions stopConditions
) {
    public GenerationParameters {
        if (temperature == null) temperature = 0.7;
        if (topP == null) topP = 1.0;
        if (stopConditions == null) stopConditions = StopConditions.none();
    }

    public static GenerationParameters defaults() {
        return new GenerationParameters(0.7, null, 1.0, null, StopConditions.none());
    }

    public static GenerationParameters deterministic() {
        return new GenerationParameters(0.0, null, 1.0, null, StopConditions.none());
    }
}
