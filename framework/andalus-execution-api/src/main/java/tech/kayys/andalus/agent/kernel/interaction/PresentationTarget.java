
package tech.kayys.andalus.agent.kernel.interaction;

public record PresentationTarget(
        String surfaceId,
        String region
) {

    public PresentationTarget {
        if (surfaceId == null || surfaceId.isBlank()) {
            throw new IllegalArgumentException(
                    "surfaceId must not be blank"
            );
        }

        region = region == null
                ? "main"
                : region;
    }

    public static PresentationTarget main(
            String surfaceId
    ) {
        return new PresentationTarget(
                surfaceId,
                "main"
        );
    }
}
