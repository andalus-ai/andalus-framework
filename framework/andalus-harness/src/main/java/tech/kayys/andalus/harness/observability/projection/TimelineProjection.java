package tech.kayys.andalus.harness.observability.projection;

import tech.kayys.andalus.harness.observability.event.AndalusEvent;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * Ordered timeline projection for visualization and debugging.
 */
public record TimelineProjection(List<TimelineItem> items) {

    public record TimelineItem(
            Instant timestamp,
            long sequence,
            String domain,
            String action,
            String summary
    ) {}

    public static TimelineProjection from(Collection<AndalusEvent> events) {
        if (events == null || events.isEmpty()) {
            return new TimelineProjection(List.of());
        }

        List<TimelineItem> list = events.stream()
                .sorted((a, b) -> a.sequence().compareTo(b.sequence()))
                .map(e -> new TimelineItem(
                        e.timestamp(),
                        e.sequence().value(),
                        e.type().domain().name(),
                        e.type().action(),
                        e.type().canonicalName()
                ))
                .toList();

        return new TimelineProjection(list);
    }
}
