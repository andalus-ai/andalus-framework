
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Objects;

public record InteractionCorrelation(
        String runId,
        String executionId,
        String stepId,
        String invocationId
) {

    public InteractionCorrelation {
        Objects.requireNonNull(
                runId,
                "runId"
        );

        if (executionId == null || executionId.isBlank()) {
            executionId = null;
        }

        if (stepId == null || stepId.isBlank()) {
            stepId = null;
        }

        if (invocationId == null || invocationId.isBlank()) {
            invocationId = null;
        }
    }

    public static InteractionCorrelation run(
            String runId
    ) {
        return new InteractionCorrelation(
                runId,
                null,
                null,
                null
        );
    }

    public InteractionCorrelation execution(
            String executionId
    ) {
        return new InteractionCorrelation(
                runId,
                executionId,
                stepId,
                invocationId
        );
    }

    public InteractionCorrelation step(
            String stepId
    ) {
        return new InteractionCorrelation(
                runId,
                executionId,
                stepId,
                invocationId
        );
    }

    public InteractionCorrelation invocation(
            String invocationId
    ) {
        return new InteractionCorrelation(
                runId,
                executionId,
                stepId,
                invocationId
        );
    }
}
