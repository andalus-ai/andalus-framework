package tech.kayys.wayang.execution.governance.approval;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.tool.SimpleToolInvocation;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApprovalServiceTest {

    private InMemoryApprovalStore store;
    private DefaultApprovalService service;

    @BeforeEach
    void setUp() {
        store = new InMemoryApprovalStore();
        service = new DefaultApprovalService(store, Clock.systemUTC(), Duration.ofMinutes(10));
    }

    private SimpleToolInvocation invocation() {
        return SimpleToolInvocation.of("filesystem.write", Map.of("path", "/workspace/file.txt"));
    }

    private ApprovalRequestTemplate template() {
        return new ApprovalRequestTemplate(
                invocation(),
                "tenant-1",
                "user-1",
                "agent-1",
                "exec-1",
                "corr-1",
                "Human review required",
                Duration.ofMinutes(10),
                Map.of()
        );
    }

    @Test
    void requestCreatesPendingApproval() {
        ApprovalRequest req = service.request(template());
        assertNotNull(req.id());
        assertEquals(ApprovalStatus.PENDING, req.status());
        assertTrue(req.pending());
        assertFalse(req.terminal());
        assertFalse(service.isApproved(req.id()));
    }

    @Test
    void approveTransition() {
        ApprovalRequest req = service.request(template());
        ApprovalRequest approved = service.approve(req.id(), "admin-1", "LGTM");

        assertEquals(ApprovalStatus.APPROVED, approved.status());
        assertEquals("admin-1", approved.decidedBy());
        assertEquals("LGTM", approved.decisionReason());
        assertTrue(approved.terminal());
        assertTrue(service.isApproved(req.id()));
    }

    @Test
    void rejectTransition() {
        ApprovalRequest req = service.request(template());
        ApprovalRequest rejected = service.reject(req.id(), "admin-1", "Dangerous");

        assertEquals(ApprovalStatus.REJECTED, rejected.status());
        assertTrue(rejected.terminal());
        assertFalse(service.isApproved(req.id()));
    }

    @Test
    void cancelTransition() {
        ApprovalRequest req = service.request(template());
        ApprovalRequest cancelled = service.cancel(req.id(), "Workflow aborted");

        assertEquals(ApprovalStatus.CANCELLED, cancelled.status());
        assertTrue(cancelled.terminal());
    }

    @Test
    void cannotTransitionTerminalApproval() {
        ApprovalRequest req = service.request(template());
        service.approve(req.id(), "admin-1", "ok");

        assertThrows(IllegalStateException.class, () -> service.reject(req.id(), "admin-2", "not ok"));
    }

    @Test
    void expiredApprovalCannotBeApproved() {
        Instant t0 = Instant.parse("2026-09-17T10:00:00Z");
        MutableClock clock = new MutableClock(t0);
        DefaultApprovalService timedService = new DefaultApprovalService(store, clock, Duration.ofMinutes(5));

        ApprovalRequest req = timedService.request(template());

        // Fast forward past expiration
        clock.set(t0.plus(Duration.ofMinutes(15)));

        assertThrows(ApprovalExpiredException.class, () -> timedService.approve(req.id(), "admin", "ok"));
    }

    @Test
    void notFoundThrowsException() {
        assertThrows(ApprovalNotFoundException.class, () -> service.get("non-existent"));
    }

    private static class MutableClock extends Clock {
        private Instant current;

        MutableClock(Instant initial) {
            this.current = initial;
        }

        void set(Instant instant) {
            this.current = instant;
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return current;
        }
    }
}
