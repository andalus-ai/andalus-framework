package tech.kayys.wayang.harness.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.mobility.FencingToken;
import tech.kayys.wayang.state.core.session.InMemoryDurableSessionStore;
import tech.kayys.wayang.state.session.DurableSessionStoreSPI;
import tech.kayys.wayang.state.session.SessionControlHeader;
import tech.kayys.wayang.state.session.SessionId;

class SessionMigrationHarnessTest {

    private DurableSessionStoreSPI sessionStore;
    private SessionMigrationHarness harness;

    @BeforeEach
    void setUp() {
        sessionStore = new InMemoryDurableSessionStore();
        harness = new SessionMigrationHarness(sessionStore);
    }

    @Test
    void testSuccessfulCrashAndResumeUnderNewFencingToken() {
        SessionId sessionId = SessionId.generate();
        SessionControlHeader initialHeader = SessionControlHeader.create(sessionId, "tenant-1", "worker-a");
        sessionStore.initializeSession(initialHeader);

        FencingToken oldToken = initialHeader.fencingToken();
        FencingToken newToken = oldToken.next();

        MigrationVerificationResult result = harness.simulateCrashAndResume(
            sessionId,
            "worker-a",
            "worker-b",
            oldToken,
            newToken
        );

        assertThat(result.success()).isTrue();
        assertThat(result.details()).contains("worker-b");

        SessionControlHeader header = sessionStore.getHeader(sessionId).orElseThrow();
        assertThat(header.assignedWorkerId()).isEqualTo("worker-b");
        assertThat(header.fencingToken()).isEqualTo(newToken);
    }

    @Test
    void testRejectionWhenTokenIsNotMonotonic() {
        SessionId sessionId = SessionId.generate();
        SessionControlHeader initialHeader = SessionControlHeader.create(sessionId, "tenant-1", "worker-a");
        sessionStore.initializeSession(initialHeader);

        FencingToken oldToken = FencingToken.of(5L);
        FencingToken invalidToken = FencingToken.of(3L);

        MigrationVerificationResult result = harness.simulateCrashAndResume(
            sessionId,
            "worker-a",
            "worker-b",
            oldToken,
            invalidToken
        );

        assertThat(result.success()).isFalse();
        assertThat(result.details()).contains("strictly greater");
    }
}
