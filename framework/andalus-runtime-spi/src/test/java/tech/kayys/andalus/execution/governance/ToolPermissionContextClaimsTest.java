package tech.kayys.andalus.execution.governance;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ToolPermissionContextClaimsTest {

    @Test
    void exposesExplicitPermissionsToPolicy() {
        ToolPermissionContext context = new ToolPermissionContext(
                "tenant-a", "user-a", "execution-a", List.of("agent:run"),
                "storage.read", ToolCapabilityLevel.READ, Set.of("storage:read"));

        assertThat(context.hasRole("agent:run")).isTrue();
        assertThat(context.hasPermission("storage:read")).isTrue();
        assertThat(context.hasPermission("storage:write")).isFalse();
    }

    @Test
    void standaloneContextDoesNotManufactureWildcardRole() {
        ToolPermissionContext context = ToolPermissionContext.standalone(
                "execution-a", "storage.read", ToolCapabilityLevel.READ);

        assertThat(context.roles()).isEmpty();
        assertThat(context.permissions()).isEmpty();
    }

    @Test
    void rejectsWildcardAuthorizationClaims() {
        assertThatThrownBy(() -> new ToolPermissionContext(
                "tenant-a", "user-a", "execution-a", List.of("*"),
                "storage.read", ToolCapabilityLevel.READ, Set.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ToolPermissionContext(
                "tenant-a", "user-a", "execution-a", List.of(),
                "storage.read", ToolCapabilityLevel.READ, Set.of("*")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
