package tech.kayys.wayang.harness.context;

import java.util.List;
import java.util.Objects;

/**
 * Represents a context contribution.
 *
 * <p>Its components capture `source id`, `priority`, `items`.</p>
 *
 * @param sourceId the source id
 * @param priority the priority
 * @param items the items
 */


public record ContextContribution(
        String sourceId,
        ContextPriority priority,
        List<ContextItem> items
) {
    public ContextContribution {
        Objects.requireNonNull(sourceId, "sourceId");
        priority = priority == null ? ContextPriority.NORMAL : priority;
        items = items == null ? List.of() : List.copyOf(items);
    }

    public static ContextContribution of(String sourceId, List<ContextItem> items) {
        return new ContextContribution(sourceId, ContextPriority.NORMAL, items);
    }
}
