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
        BlackboardEntry entry = blackboard.post("investigation", "agent-1", "app/json", "{\"finding\":\"ok\"}");

        assertThat(entry.entryId()).isNotNull();
        assertThat(entry.topic()).isEqualTo("investigation");
        assertThat(blackboard.get(entry.entryId())).isPresent();
        assertThat(blackboard.getByTopic("investigation")).hasSize(1);
    }

    @Test
    void testClaimAndAtomicUpdate() {
        BlackboardEntry entry = blackboard.post("plan", "agent-1", "app/json", "{\"status\":\"draft\"}");

        boolean claimed = blackboard.claimEntry(entry.entryId(), "agent-2", 5000);
        assertThat(claimed).isTrue();

        // Agent 3 cannot claim while agent 2 holds the lease
        boolean blockedClaim = blackboard.claimEntry(entry.entryId(), "agent-3", 5000);
        assertThat(blockedClaim).isFalse();

        // Agent 2 can update
        BlackboardEntry updated = blackboard.updateClaimed(entry.entryId(), "agent-2", "{\"status\":\"approved\"}");
        assertThat(updated.payloadJson()).contains("approved");
        assertThat(updated.version()).isEqualTo(2L);

        // Agent 3 cannot update without claim
        assertThatThrownBy(() -> blackboard.updateClaimed(entry.entryId(), "agent-3", "{\"status\":\"corrupted\"}"))
            .isInstanceOf(IllegalStateException.class);

        // Agent 2 releases claim, now agent 3 can claim
        blackboard.releaseClaim(entry.entryId(), "agent-2");
        assertThat(blackboard.claimEntry(entry.entryId(), "agent-3", 5000)).isTrue();
    }

    @Test
    void testTopicSubscription() {
        AtomicInteger notifications = new AtomicInteger(0);
        AutoCloseable sub = blackboard.subscribe("metrics", e -> notifications.incrementAndGet());

        blackboard.post("metrics", "agent-1", "app/json", "{\"cpu\":10}");
        blackboard.post("other-topic", "agent-1", "app/json", "{}");
        blackboard.post("metrics", "agent-2", "app/json", "{\"cpu\":20}");

        assertThat(notifications.get()).isEqualTo(2);
    }
}
