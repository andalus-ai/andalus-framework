package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolInvocation;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Rich context passed to {@link ToolPolicy} during evaluation, including identity,
 * execution metadata, requested permissions, and target resources.
 */
public record PolicyEvaluationContext(
        ToolPermissionContext permissionContext,
        ToolInvocation invocation,
        String tenantId,
        String userId,
        String agentId,
        Set<String> roles,
        String executionId,
        String correlationId,
        String parentExecutionId,
        Instant deadline,
        Map<String, String> resources,
        Map<String, Object> attributes
) {

    public PolicyEvaluationContext {
        Objects.requireNonNull(permissionContext, "permissionContext cannot be null");
        Objects.requireNonNull(invocation, "invocation cannot be null");

        tenantId = normalize(tenantId);
        userId = normalize(userId);
        agentId = normalize(agentId);
        executionId = normalize(executionId);
        correlationId = normalize(correlationId);
        parentExecutionId = normalize(parentExecutionId);

        roles = roles == null ? Set.of() : Set.copyOf(roles);
        resources = resources == null ? Map.of() : Map.copyOf(resources);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public Optional<String> tenantIdOptional() {
        return Optional.ofNullable(tenantId);
    }

    public Optional<String> userIdOptional() {
        return Optional.ofNullable(userId);
    }

    public Optional<String> agentIdOptional() {
        return Optional.ofNullable(agentId);
    }

    public Optional<String> executionIdOptional() {
        return Optional.ofNullable(executionId);
    }

    public Optional<String> correlationIdOptional() {
        return Optional.ofNullable(correlationId);
    }

    public Optional<String> parentExecutionIdOptional() {
        return Optional.ofNullable(parentExecutionId);
    }

    public Optional<Instant> deadlineOptional() {
        return Optional.ofNullable(deadline);
    }

    public Optional<String> resource(String permissionId) {
        if (permissionId == null || permissionId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(resources.get(permissionId.trim()));
    }

    public Optional<Object> attribute(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(attributes.get(name.trim()));
    }

    public boolean hasRole(String role) {
        if (role == null || role.isBlank()) {
            return false;
        }
        return roles.contains(role.trim());
    }

    public boolean deadlineExceeded(Instant now) {
        if (deadline == null) {
            return false;
        }
        Objects.requireNonNull(now, "now cannot be null");
        return !now.isBefore(deadline);
    }

    public boolean toolMatchesPermissionContext() {
        String contextTool = permissionContext.toolName();
        if (contextTool == null || contextTool.isBlank()) {
            return true;
        }
        return contextTool.equals(invocation.name());
    }
}
