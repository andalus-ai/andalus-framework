package tech.kayys.andalus.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.recovery.RecoveryFailureCategory;
import tech.kayys.andalus.execution.recovery.RecoveryRetryDecision;
import tech.kayys.andalus.execution.recovery.RecoveryRetryPolicy;
import tech.kayys.andalus.execution.recovery.RecoverySchedule;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RecoveryRetryPolicyTest {

    private final Instant now = Instant.parse("2026-11-02T10:00:00Z");
    private final RecoveryRetryPolicy policy = new RecoveryRetryPolicy(
            4, Duration.ofSeconds(2), Duration.ofSeconds(10), 2.0d, 0.25d);

    @Test
    void appliesBoundedExponentialBackoffWithDeterministicJitter() {
        var first = policy.evaluate(RecoveryFailureCategory.TRANSIENT, 1, now,
                Optional.empty(), false, 0.5d);
        var second = policy.evaluate(RecoveryFailureCategory.TRANSIENT, 2, now,
                Optional.empty(), false, 0.5d);
        var capped = policy.evaluate(RecoveryFailureCategory.TRANSIENT, 3, now,
                Optional.empty(), false, 1.0d);

        assertEquals(RecoveryRetryDecision.Outcome.RETRY, first.outcome());
        assertEquals(now.plusSeconds(2), first.nextAttemptAt().orElseThrow());
        assertEquals(now.plusSeconds(4), second.nextAttemptAt().orElseThrow());
        assertEquals(now.plusSeconds(10), capped.nextAttemptAt().orElseThrow());
    }

    @Test
    void doesNotRetryPermanentOrOperatorActionFailures() {
        assertEquals(RecoveryRetryDecision.Outcome.DO_NOT_RETRY,
                policy.evaluate(RecoveryFailureCategory.PERMANENT, 1, now,
                        Optional.empty(), false, 0.5d).outcome());
        assertEquals(RecoveryRetryDecision.Outcome.DO_NOT_RETRY,
                policy.evaluate(RecoveryFailureCategory.OPERATOR_ACTION_REQUIRED, 1, now,
                        Optional.empty(), false, 0.5d).outcome());
    }

    @Test
    void deadLettersWhenMaximumAttemptsAreExhausted() {
        var decision = policy.evaluate(RecoveryFailureCategory.TRANSIENT, 4, now,
                Optional.empty(), false, 0.5d);
        assertEquals(RecoveryRetryDecision.Outcome.DEAD_LETTER, decision.outcome());
        assertTrue(decision.nextAttemptAt().isEmpty());
    }

    @Test
    void cancellationAndExpiredOrInsufficientDeadlinesPreventRetry() {
        assertEquals(RecoveryRetryDecision.Outcome.CANCELLED,
                policy.evaluate(RecoveryFailureCategory.TRANSIENT, 1, now,
                        Optional.empty(), true, 0.5d).outcome());
        assertEquals(RecoveryRetryDecision.Outcome.DEADLINE_EXCEEDED,
                policy.evaluate(RecoveryFailureCategory.TRANSIENT, 1, now,
                        Optional.of(now), false, 0.5d).outcome());
        assertEquals(RecoveryRetryDecision.Outcome.DEADLINE_EXCEEDED,
                policy.evaluate(RecoveryFailureCategory.TRANSIENT, 1, now,
                        Optional.of(now.plusSeconds(2)), false, 0.5d).outcome());
    }

    @Test
    void rejectsInvalidJitterAndScheduleIdentity() {
        assertThrows(IllegalArgumentException.class, () -> policy.evaluate(
                RecoveryFailureCategory.TRANSIENT, 1, now, Optional.empty(), false, 1.1d));
        assertThrows(IllegalArgumentException.class, () -> new RecoverySchedule(
                " ", "execution-1", 1, RecoveryFailureCategory.TRANSIENT, now, Optional.empty()));
    }
}
