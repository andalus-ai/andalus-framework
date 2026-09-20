package tech.kayys.wayang.harness.session;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import tech.kayys.wayang.execution.mobility.FencingToken;
import tech.kayys.wayang.state.session.DurableSessionStoreSPI;
import tech.kayys.wayang.state.session.SessionControlHeader;
import tech.kayys.wayang.state.session.SessionId;
import tech.kayys.wayang.state.session.SessionSnapshot;
import tech.kayys.wayang.state.session.SessionStatus;

/**
 * Test harness fixture for simulating worker crashes, fencing token invalidation,
 * and resilient session resumption.
 * <p>
 * Strictly serves as a test driver and simulation utility; does NOT manage production session state.
 */
public class SessionMigrationHarness {

    private final DurableSessionStoreSPI sessionStore;

    public SessionMigrationHarness(DurableSessionStoreSPI sessionStore) {
        this.sessionStore = Objects.requireNonNull(sessionStore, "sessionStore cannot be null");
    }

    /**
     * Simulates an abrupt worker failure and verifies that a target worker can safely take over
     * under a new fencing token while rejecting stale writes from the crashed worker.
     */
    public MigrationVerificationResult simulateCrashAndResume(
        SessionId sessionId,
        String crashedWorkerId,
        String targetWorkerId,
        FencingToken oldToken,
        FencingToken newToken
    ) {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(crashedWorkerId, "crashedWorkerId cannot be null");
        Objects.requireNonNull(targetWorkerId, "targetWorkerId cannot be null");
        Objects.requireNonNull(oldToken, "oldToken cannot be null");
        Objects.requireNonNull(newToken, "newToken cannot be null");

        if (!newToken.isAfter(oldToken)) {
            return MigrationVerificationResult.failure("New fencing token must be strictly greater than old token");
        }

        // 1. Verify current session exists
        Optional<SessionControlHeader> headerOpt = sessionStore.getHeader(sessionId);
        if (headerOpt.isEmpty()) {
            return MigrationVerificationResult.failure("Session not found: " + sessionId);
        }

        // 2. Simulate target worker acquiring lease under new fencing token
        boolean leaseAcquired = sessionStore.updateLease(sessionId, targetWorkerId, newToken);
        if (!leaseAcquired) {
            return MigrationVerificationResult.failure("Target worker failed to acquire lease with token: " + newToken);
        }

        // 3. Simulate crashed worker attempting late write with stale token -> Must be rejected
        SessionSnapshot staleSnapshot = SessionSnapshot.of(
            headerOpt.get().withWorker(crashedWorkerId, oldToken),
            "stale-context-ref",
            "stale-memory-ref",
            List.of()
        );

        boolean staleWriteRejected = false;
        try {
            sessionStore.recordSnapshot(staleSnapshot, oldToken);
        } catch (Exception expected) {
            staleWriteRejected = true;
        }

        if (!staleWriteRejected) {
            return MigrationVerificationResult.failure("Stale write from crashed worker was NOT fenced out!");
        }

        // 4. Target worker records healthy snapshot under new token
        SessionControlHeader activeHeader = sessionStore.getHeader(sessionId).orElseThrow();
        SessionSnapshot healthySnapshot = SessionSnapshot.of(
            activeHeader,
            "resumed-context-ref",
            "resumed-memory-ref",
            List.of()
        );
        sessionStore.recordSnapshot(healthySnapshot, newToken);

        // 5. Verify recovery
        SessionControlHeader finalHeader = sessionStore.getHeader(sessionId).orElseThrow();
        return MigrationVerificationResult.ok(
            "Session successfully migrated to worker " + targetWorkerId + " under epoch " + newToken.generation(),
            finalHeader.stateRevision()
        );
    }
}
