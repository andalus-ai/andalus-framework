package tech.kayys.andalus.harness.learning;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFeedbackEvidenceStoreTest {

    @Test
    void reservesWholeBatchAndResolvesOwner() {
        var store = new InMemoryFeedbackEvidenceStore();

        var result = store.reserve("signal-1", List.of("trace:1", "trace:2"));

        assertThat(result.status()).isEqualTo(FeedbackEvidenceStore.ReservationStatus.RESERVED);
        assertThat(store.findOwner("trace:1")).contains("signal-1");
        assertThat(store.findOwner("trace:2")).contains("signal-1");
    }

    @Test
    void exactRetryIsIdempotentAndChangedBatchIsRejected() {
        var store = new InMemoryFeedbackEvidenceStore();
        store.reserve("signal-1", List.of("trace:1"));

        assertThat(store.reserve("signal-1", List.of("trace:1")).status())
                .isEqualTo(FeedbackEvidenceStore.ReservationStatus.ALREADY_RESERVED_BY_SAME_SIGNAL);
        assertThat(store.reserve("signal-1", List.of("trace:2")).status())
                .isEqualTo(FeedbackEvidenceStore.ReservationStatus.EVIDENCE_ALREADY_CONSUMED);
        assertThat(store.findOwner("trace:2")).isEmpty();
    }

    @Test
    void conflictingBatchDoesNotPartiallyReserveReferences() {
        var store = new InMemoryFeedbackEvidenceStore();
        store.reserve("signal-1", List.of("trace:shared"));

        var conflict = store.reserve("signal-2", List.of("trace:new", "trace:shared"));

        assertThat(conflict.status()).isEqualTo(FeedbackEvidenceStore.ReservationStatus.EVIDENCE_ALREADY_CONSUMED);
        assertThat(store.findOwner("trace:new")).isEmpty();
    }

    @Test
    void rejectsInvalidBatches() {
        var store = new InMemoryFeedbackEvidenceStore();

        assertThat(store.reserve("signal-1", List.of()).status())
                .isEqualTo(FeedbackEvidenceStore.ReservationStatus.INVALID_REQUEST);
        assertThat(store.reserve("signal-2", List.of("trace:1", "trace:1")).status())
                .isEqualTo(FeedbackEvidenceStore.ReservationStatus.INVALID_REQUEST);
        assertThat(store.reserve(" ", List.of("trace:1")).status())
                .isEqualTo(FeedbackEvidenceStore.ReservationStatus.INVALID_REQUEST);
    }
}
