package tech.kayys.andalus.context.assembly;

import java.util.Map;
import tech.kayys.andalus.context.compaction.ContextSlice;

/**
 * Serializes a ContextEnvelope into structured text with clear demarcation.
 */
public final class DefaultTextContextSerializer implements ContextSerializer<String> {

    @Override
    public String serialize(ContextEnvelope envelope, Map<String, Object> options) {
        if (envelope == null || envelope.slices().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (ContextSlice slice : envelope.slices()) {
            if (slice.systemPrompt() != null && !slice.systemPrompt().isBlank()) {
                sb.append("=== SYSTEM INSTRUCTIONS ===\n")
                  .append(slice.systemPrompt())
                  .append("\n\n");
            }
            if (!slice.historicalSummaries().isEmpty()) {
                sb.append("=== CONTEXT SUMMARY ===\n");
                for (String summary : slice.historicalSummaries()) {
                    sb.append("- ").append(summary).append("\n");
                }
                sb.append("\n");
            }
            if (!slice.activeTurns().isEmpty()) {
                sb.append("=== RECENT TURNS ===\n");
                for (String turn : slice.activeTurns()) {
                    sb.append(turn).append("\n");
                }
                sb.append("\n");
            }
        }
        return sb.toString().trim();
    }
}
