package tech.kayys.andalus.harness.learning;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFeedbackEvidenceStoreTest {
    @Test
    void reservesEvidenceAndMakesExactRetriesIdempotent() {
        FeedbackEvidenceStore store = new InMemoryFeedbackEvidenceStore();
        assertThat(store.reserve("signal-a", List.of("trace:1", "trace:2")).status())
                .isEqualTo(FeedbackEvidenceStore.ReservationStatus.RESERVED);
        assertThat(store.reserve("signal-a", List.of("trace:1", "trace:2")).status())
                .isEqualTo(FeedbackEvidenceStore.ReservationStatus.ALREADY_RESERVED_BY_SAME_SIGNAL);
        assertThat(store.findOwner("trace:1")).contains("signal-a");
    }

    @Test
    void rejectsReplayAndDoesNotPartiallyReserveConflictingBatch() {
        FeedbackEvidenceStore store = new InMemoryFeedbackEvidenceStore();
        store.reserve("signal-a", List.of("trace:shared"));
        assertThat(store.reserve("signal-b", List.of("trace:new", "trace:shared")).status())
                .isEqualTo(FeedbackEvidenceStore.ReservationStatus.EVIDENCE_ALREADY_CONSUMED);
        assertThat(store.findOwner("trace:new")).isEmpty();
    }

    @Test
    void rejectsInvalidEvidenceReferences() {
        FeedbackEvidenceStore store = new InMemoryFeedbackEvidenceStore();
        assertThat(store.reserve("signal-a", List.of()).status())
                .isEqualTo(FeedbackEvidenceStore.ReservationStatus.INVALID_REQUEST);
        assertThat(store.reserve("signal-b", List.of("trace:1", "trace:1")).status())
                .isEqualTo(FeedbackEvidenceStore.ReservationStatus.INVALID_REQUEST);
        assertThat(store.reserve("signal-c", List.of(" ")).status())
                .isEqualTo(FeedbackEvidenceStore.ReservationStatus.INVALID_REQUEST);
    }
}
