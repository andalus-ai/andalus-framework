package tech.kayys.andalus.execution;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgentCallerContextTest {

    @Test
    void createsImmutableAuthenticatedUserClaims() {
        var roles = new java.util.HashSet<>(Set.of("agent:run"));
        var permissions = new java.util.HashSet<>(Set.of("tools:read"));
        AgentCallerContext context = AgentCallerContext.user(
                "tenant-a", "user-a", roles, permissions);

        roles.add("admin");
        permissions.add("tools:write");

        assertThat(context.tenantId()).isEqualTo("tenant-a");
        assertThat(context.userId()).isEqualTo("user-a");
        assertThat(context.principalType()).isEqualTo(AgentCallerContext.PrincipalType.USER);
        assertThat(context.principalId()).isEqualTo("user-a");
        assertThat(context.roles()).containsExactly("agent:run");
        assertThat(context.permissions()).containsExactly("tools:read");
        assertThatThrownBy(() -> context.roles().add("admin"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void anonymousCallerCannotCarryAuthorizationClaims() {
        assertThat(AgentCallerContext.anonymous("tenant-a").roles()).isEmpty();
        assertThatThrownBy(() -> new AgentCallerContext(
                "tenant-a", null, AgentCallerContext.PrincipalType.ANONYMOUS,
                null, Set.of("agent:run"), Set.of(), Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsWildcardAndBlankClaims() {
        assertThatThrownBy(() -> AgentCallerContext.user(
                "tenant-a", "user-a", Set.of("*"), Set.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> AgentCallerContext.user(
                "tenant-a", "user-a", Set.of(), Set.of(" ")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void requiresPrincipalIdentityForAuthenticatedUser() {
        assertThatThrownBy(() -> new AgentCallerContext(
                "tenant-a", null, AgentCallerContext.PrincipalType.USER,
                null, Set.of(), Set.of(), Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
