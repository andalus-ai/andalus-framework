package tech.kayys.andalus.spi.agent;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Non-semantic descriptive metadata associated with an agent.
 */
public record AgentMetadata(
        String description,
        String versionLabel,
        Map<String, String> labels
) {

    public AgentMetadata {
        description = description == null ? "" : description;
        versionLabel = versionLabel == null ? "" : versionLabel;
        labels = labels == null
                ? Map.of()
                : Map.copyOf(labels);
    }

    public static AgentMetadata empty() {
        return new AgentMetadata(
                "",
                "",
                Map.of());
    }

    public static AgentMetadata of(
            String description,
            Map<String, String> labels) {

        return new AgentMetadata(
                description,
                "",
                labels);
    }

    public static AgentMetadata of(
            String description,
            Set<String> tags) {

        Map<String, String> labelMap = tags == null
                ? Map.of()
                : tags.stream().collect(Collectors.toMap(t -> t, t -> ""));

        return new AgentMetadata(
                description,
                "",
                labelMap);
    }
}
