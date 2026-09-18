package tech.kayys.wayang.harness.model;

import java.util.Objects;

/**
 * Represents a model intent.
 *
 * <p>Its components capture `id`, `task`, `requirements`, `input`, `context`.</p>
 *
 * @param id the id
 * @param task the task
 * @param requirements the requirements
 * @param input the input
 * @param context the context
 */


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
