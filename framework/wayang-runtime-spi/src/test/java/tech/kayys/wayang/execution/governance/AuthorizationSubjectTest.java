package tech.kayys.wayang.execution.governance;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationSubjectTest {

    @Test
    void normalizationAndTrimming() {
        AuthorizationSubject subject = new AuthorizationSubject(
                "  tenant-1  ",
                "  user-1  ",
                "  agent-1  ",
                List.of("  admin  ", "developer", " admin ")
        );

        assertEquals("tenant-1", subject.tenantId());
        assertEquals("user-1", subject.userId());
        assertEquals("agent-1", subject.agentId());
        assertEquals(2, subject.roles().size());
        assertTrue(subject.hasRole("admin"));
        assertTrue(subject.hasRole("developer"));
        assertFalse(subject.hasRole("operator"));
        assertFalse(subject.isAnonymous());
    }

    @Test
    void anonymousSubject() {
        AuthorizationSubject subject = AuthorizationSubject.standalone("my-agent");
        assertTrue(subject.isAnonymous());
        assertNull(subject.tenantId());
        assertEquals("my-agent", subject.agentId());
        assertTrue(subject.hasRole("*"));
    }
}
