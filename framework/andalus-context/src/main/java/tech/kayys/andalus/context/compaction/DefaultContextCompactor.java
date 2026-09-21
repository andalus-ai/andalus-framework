package tech.kayys.andalus.context.compaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Default heuristic context compactor that preserves system prompt rules and recent turns
 * while folding older conversation history into synthesized summary blocks.
 */
public class DefaultContextCompactor implements ContextCompactor {

    private final int minRecentTurnsToKeep;

    public DefaultContextCompactor() {
        this(4);
    }

    public DefaultContextCompactor(int minRecentTurnsToKeep) {
        this.minRecentTurnsToKeep = Math.max(1, minRecentTurnsToKeep);
    }

    @Override
    public ContextSlice compact(ContextSlice slice, long targetTokenBudget) {
        Objects.requireNonNull(slice, "slice cannot be null");
        if (slice.estimatedTokens() <= targetTokenBudget || slice.activeTurns().size() <= minRecentTurnsToKeep) {
            return slice;
        }

        int totalTurns = slice.activeTurns().size();
        int splitPoint = Math.max(1, totalTurns - minRecentTurnsToKeep);

        List<String> turnsToSummarize = slice.activeTurns().subList(0, splitPoint);
        List<String> retainedTurns = new ArrayList<>(slice.activeTurns().subList(splitPoint, totalTurns));

        // Synthesize summary entry
        String summary = "[Compacted " + turnsToSummarize.size() + " prior turns. Earliest: " +
            (turnsToSummarize.isEmpty() ? "" : truncate(turnsToSummarize.get(0), 100)) + "]";

        List<String> newSummaries = new ArrayList<>(slice.historicalSummaries());
        newSummaries.add(summary);

        // Estimate reduced tokens (heuristic: 25% of summarized turns + retained turns + prompt)
        long promptTokens = slice.systemPrompt().length() / 4;
        long retainedTokens = retainedTurns.stream().mapToLong(t -> t.length() / 4).sum();
        long summaryTokens = newSummaries.stream().mapToLong(s -> s.length() / 4).sum();
        long newEstimate = promptTokens + retainedTokens + summaryTokens;

        return new ContextSlice(
            slice.sliceId(),
            slice.systemPrompt(),
            retainedTurns,
            newSummaries,
            newEstimate,
            slice.maxTokenCapacity()
        );
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }
}
