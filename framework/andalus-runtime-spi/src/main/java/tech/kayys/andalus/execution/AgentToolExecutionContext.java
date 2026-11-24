package tech.kayys.andalus.execution;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import tech.kayys.andalus.execution.governance.ToolBudget;

/**
 * Immutable identity and policy snapshot for one agent execution's tool calls.
 *
 * <p>The context is created once by the execution owner and passed explicitly
 * through every tool invocation. It must never be stored in mutable fields on
 * an application-scoped executor or propagated through thread-local state.</p>
 *
 * <p>{@link ToolBudget} is intentionally an execution-scoped, thread-safe
 * accounting object. The reference is final; its counters remain mutable so
 * concurrent tool calls can consume the same execution budget safely.</p>
 *
 * @param executionId stable ID of the owning execution
 * @param correlationId trace/correlation ID
 * @param parentExecutionId optional parent execution ID
 * @param tenantId tenant namespace, if applicable
 * @param userId authenticated user, if applicable
 * @param agentId stable agent definition ID
 * @param principalType kind of principal executing the agent
 * @param principalId stable principal identifier (required except for anonymous)
 * @param roles effective roles; wildcard roles are not implicitly added
 * @param executionBudget resource limits for this execution
 * @param toolBudget shared, execution-scoped tool accounting budget
 * @param constraints immutable agent/runtime constraints
 * @param attributes immutable metadata for policy, audit and cache
 * @param createdAt creation timestamp
 */
public record AgentToolExecutionContext(
        String executionId,
        String correlationId,
        String parentExecutionId,
        String tenantId,
        String userId,
        String agentId,
        PrincipalType principalType,
        String principalId,
        Set<String> roles,
        Set<String> permissions,
        ExecutionBudget executionBudget,
        ToolBudget toolBudget,
        Map<String, Object> constraints,
        Map<String, Object> attributes,
        Instant createdAt) {

    public AgentToolExecutionContext {
        executionId = required(executionId, "executionId");
        agentId = required(agentId, "agentId");
        principalType = Objects.requireNonNull(principalType, "principalType");
        roles = roles == null ? Set.of() : Set.copyOf(roles);
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
        constraints = constraints == null ? Map.of() : Map.copyOf(constraints);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);

        if (roles.stream().anyMatch(role -> role == null || role.isBlank() || "*".equals(role.trim()))) {
            throw new IllegalArgumentException("roles must not contain blank or wildcard entries");
        }
        if (roles.stream().anyMatch(role -> role == null || role.isBlank() || "*".equals(role.trim()))) {
            throw new IllegalArgumentException("roles must not contain blank or wildcard entries");
        }
        if (permissions.stream().anyMatch(permission -> permission == null || permission.isBlank()
                || "*".equals(permission.trim()))) {
            throw new IllegalArgumentException("permissions must not contain blank or wildcard entries");
        }
        if (principalType != PrincipalType.ANONYMOUS) {
            principalId = required(principalId, "principalId");
        }
        if (principalType == PrincipalType.USER) {
            userId = required(userId, "userId");
        }
        if (principalType == PrincipalType.ANONYMOUS && (!roles.isEmpty() || !permissions.isEmpty())) {
            throw new IllegalArgumentException("anonymous execution cannot carry roles or permissions");
        }
    }

    /** Backwards-compatible constructor without explicit permission claims. */
    public AgentToolExecutionContext(
            String executionId, String correlationId, String parentExecutionId,
            String tenantId, String userId, String agentId, PrincipalType principalType,
            String principalId, Set<String> roles, ExecutionBudget executionBudget,
            ToolBudget toolBudget, Map<String, Object> constraints,
            Map<String, Object> attributes, Instant createdAt) {
        this(executionId, correlationId, parentExecutionId, tenantId, userId, agentId,
                principalType, principalId, roles, Set.of(), executionBudget, toolBudget,
                constraints, attributes, createdAt);
    }

    private static String required(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }

    public enum PrincipalType {
        USER,
        SERVICE,
        SYSTEM,
        ANONYMOUS
    }
}
