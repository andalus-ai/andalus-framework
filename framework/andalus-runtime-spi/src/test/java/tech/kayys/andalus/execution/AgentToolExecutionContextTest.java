package tech.kayys.andalus.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

class AgentToolExecutionContextTest {

    @Test
    void snapshotsMutableCollections() {
        Set<String> roles = new HashSet<>(Set.of("operator"));
        Map<String, Object> constraints = new HashMap<>();
        constraints.put("maxOutputChars", 1200);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("source", "api");

        AgentToolExecutionContext context = new AgentToolExecutionContext(
                "exec-1", "corr-1", null, "tenant-1", "user-1", "agent-1",
                AgentToolExecutionContext.PrincipalType.USER, "user-1",
                roles, ExecutionBudget.defaults(), null, constraints, attributes, null);

        roles.add("admin");
        constraints.put("maxOutputChars", 9999);
        attributes.put("source", "mutated");

        assertEquals(Set.of("operator"), context.roles());
        assertEquals(1200, context.constraints().get("maxOutputChars"));
        assertEquals("api", context.attributes().get("source"));
        assertThrows(UnsupportedOperationException.class, () -> context.roles().add("admin"));
        assertThrows(UnsupportedOperationException.class, () -> context.attributes().put("x", "y"));
    }

    @Test
    void rejectsWildcardRoles() {
        assertThrows(IllegalArgumentException.class, () -> new AgentToolExecutionContext(
                "exec-1", null, null, null, null, "agent-1",
                AgentToolExecutionContext.PrincipalType.SYSTEM, "system",
                Set.of("*"), null, null, Map.of(), Map.of(), null));
    }

    @Test
    void anonymousPrincipalCannotCarryRoles() {
        assertThrows(IllegalArgumentException.class, () -> new AgentToolExecutionContext(
                "exec-1", null, null, null, null, "agent-1",
                AgentToolExecutionContext.PrincipalType.ANONYMOUS, null,
                Set.of("operator"), null, null, Map.of(), Map.of(), null));
    }

    @Test
    void authenticatedUserRequiresUserId() {
        assertThrows(IllegalArgumentException.class, () -> new AgentToolExecutionContext(
                "exec-1", null, null, null, null, "agent-1",
                AgentToolExecutionContext.PrincipalType.USER, "principal-1",
                Set.of(), null, null, Map.of(), Map.of(), null));
    }
}
