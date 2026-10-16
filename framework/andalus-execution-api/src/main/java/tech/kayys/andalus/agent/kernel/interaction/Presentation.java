
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Map;
import java.util.Objects;

/**
 * Protocol-neutral presentation emitted by an AgentRun.
 *
 * @param id presentation identifier
 * @param format presentation format
 * @param content format-specific content
 * @param metadata additional presentation metadata
 */
public record Presentation(
        String id,
        PresentationFormat format,
        Object content,
        Map<String, Object> metadata
) {

    public Presentation {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(format, "format");

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public static Presentation text(
            String id,
            String value
    ) {
        return new Presentation(
                id,
                PresentationFormat.TEXT,
                value,
                Map.of()
        );
    }

    public static Presentation markdown(
            String id,
            String value
    ) {
        return new Presentation(
                id,
                PresentationFormat.MARKDOWN,
                value,
                Map.of()
        );
    }

    public static Presentation json(
            String id,
            Object value
    ) {
        return new Presentation(
                id,
                PresentationFormat.JSON,
                value,
                Map.of()
        );
    }

    public static Presentation a2ui(
            String id,
            Object message
    ) {
        return new Presentation(
                id,
                PresentationFormat.A2UI,
                message,
                Map.of()
        );
    }
}
