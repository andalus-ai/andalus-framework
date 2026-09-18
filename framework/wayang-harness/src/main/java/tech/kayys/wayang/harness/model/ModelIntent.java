package tech.kayys.wayang.harness.model;

import java.util.Objects;

public record ModelIntent(
        ModelIntentId id,
        ModelTask task,
        ModelRequirements requirements,
        ModelInput input,
        ModelRequestContext context
) {
    public ModelIntent {
        Objects.requireNonNull(id, "ModelIntentId cannot be null");
        if (task == null) task = ModelTask.CHAT;
        if (requirements == null) requirements = ModelRequirements.chat();
        Objects.requireNonNull(input, "ModelInput cannot be null");
    }

    public static ModelIntent prompt(String prompt) {
        return new ModelIntent(
                ModelIntentId.generate(),
                ModelTask.CHAT,
                ModelRequirements.chat(),
                ModelInput.of(prompt),
                null
        );
    }
}
