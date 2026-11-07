package tech.kayys.andalus.agent.orchestration.coordination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemorySemanticBlackboardTest {

    private InMemorySemanticBlackboard blackboard;

    @BeforeEach
    void setUp() {
        blackboard = new InMemorySemanticBlackboard();
    }

    @Test
    void testPostAndRetrieveEntry() {
        BlackboardEntry entry = blackboard.post("investigation", "agent-1", "app/json", "finding=ok");

        assertThat(entry.entryId()).isNotNull();
        assertThat(entry.topic()).isEqualTo("investigation");
        assertThat(blackboard.get(entry.entryId())).isPresent();
        assertThat(blackboard.getByTopic("investigation")).hasSize(1);
    }

    @Test
    void testClaimAndAtomicUpdate() {
        BlackboardEntry entry = blackboard.post("plan", "agent-1", "app/json", "status=draft");

        assertThat(blackboard.claimEntry(entry.entryId(), "agent-2", 5_000)).isTrue();
        assertThat(blackboard.claimEntry(entry.entryId(), "agent-3", 5_000)).isFalse();

        BlackboardEntry updated = blackboard.updateClaimed(entry.entryId(), "agent-2", "status=approved");
        assertThat(updated.payloadJson()).isEqualTo("status=approved");
        assertThat(updated.version()).isEqualTo(2L);

        assertThatThrownBy(() -> blackboard.updateClaimed(entry.entryId(), "agent-3", "status=corrupted"))
                .isInstanceOf(IllegalStateException.class);

        blackboard.releaseClaim(entry.entryId(), "agent-2");
        assertThat(blackboard.claimEntry(entry.entryId(), "agent-3", 5_000)).isTrue();
    }

    @Test
    void testTopicSubscription() {
        AtomicInteger notifications = new AtomicInteger();
        blackboard.subscribe("metrics", entry -> notifications.incrementAndGet());

        blackboard.post("metrics", "agent-1", "app/json", "cpu=10");
        blackboard.post("other-topic", "agent-1", "app/json", "none");
        blackboard.post("metrics", "agent-2", "app/json", "cpu=20");

        assertThat(notifications.get()).isEqualTo(2);
    }

    @Test
    void topicEntriesAreReturnedInPublicationOrder() throws Exception {
        BlackboardEntry first = blackboard.post("shared", "agent-1", "app/json", "n=1");
        Thread.sleep(2);
        BlackboardEntry second = blackboard.post("shared", "agent-2", "app/json", "n=2");

        assertThat(blackboard.getByTopic("shared"))
                .extracting(BlackboardEntry::entryId)
                .containsExactly(first.entryId(), second.entryId());
    }

    @Test
    void expiredOrForeignClaimsCannotUpdateEntry() throws Exception {
        BlackboardEntry entry = blackboard.post("shared", "agent-1", "app/json", "initial");
        assertThat(blackboard.claimEntry(entry.entryId(), "agent-2", 1)).isTrue();
        Thread.sleep(10);

        assertThatThrownBy(() -> blackboard.updateClaimed(entry.entryId(), "agent-2", "stale"))
                .isInstanceOf(IllegalStateException.class);

        assertThat(blackboard.claimEntry(entry.entryId(), "agent-3", 5_000)).isTrue();
        assertThatThrownBy(() -> blackboard.updateClaimed(entry.entryId(), "agent-2", "stale"))
                .isInstanceOf(IllegalStateException.class);

        BlackboardEntry updated = blackboard.updateClaimed(entry.entryId(), "agent-3", "owner=agent-3");
        assertThat(updated.version()).isEqualTo(2L);
        assertThat(updated.payloadJson()).isEqualTo("owner=agent-3");
    }

    @Test
    void rejectsInvalidClaimLeaseDurationAndBlankIdentity() {
        BlackboardEntry entry = blackboard.post("shared", "agent-1", "app/json", "initial");

        assertThatThrownBy(() -> blackboard.claimEntry(entry.entryId(), "agent-2", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");
        assertThatThrownBy(() -> blackboard.post(" ", "agent-1", "app/json", "invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void closingLastSubscriptionRemovesListenerRegistration() throws Exception {
        AtomicInteger notifications = new AtomicInteger();
        AutoCloseable subscription = blackboard.subscribe("events", entry -> notifications.incrementAndGet());

        blackboard.post("events", "agent-1", "app/json", "first");
        subscription.close();
        blackboard.post("events", "agent-1", "app/json", "second");

        assertThat(notifications.get()).isEqualTo(1);
    }
}
