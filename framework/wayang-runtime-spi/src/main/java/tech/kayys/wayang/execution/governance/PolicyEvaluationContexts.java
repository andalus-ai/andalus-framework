package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolInvocation;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Factory and builder for {@link PolicyEvaluationContext}.
 */
public final class PolicyEvaluationContexts {

    private PolicyEvaluationContexts() {
    }

    public static PolicyEvaluationContext create(
            ToolPermissionContext permissionContext,
            ToolInvocation invocation) {

        Objects.requireNonNull(permissionContext, "permissionContext cannot be null");
        Objects.requireNonNull(invocation, "invocation cannot be null");

        Set<String> roles = permissionContext.roles() != null
                ? new HashSet<>(permissionContext.roles())
                : Set.of();

        return new PolicyEvaluationContext(
                permissionContext,
                invocation,
                permissionContext.tenantId(),
                permissionContext.userId(),
                null,
                roles,
                permissionContext.executionId(),
                null,
                null,
                null,
                Map.of(),
                Map.of()
        );
    }

    public static Builder builder(
            ToolPermissionContext permissionContext,
            ToolInvocation invocation) {

        return new Builder(permissionContext, invocation);
    }

    public static final class Builder {

        private final ToolPermissionContext permissionContext;
        private final ToolInvocation invocation;

        private String tenantId;
        private String userId;
        private String agentId;
        private Set<String> roles;
        private String executionId;
        private String correlationId;
        private String parentExecutionId;
        private Instant deadline;
        private Map<String, String> resources = Map.of();
        private Map<String, Object> attributes = Map.of();

        private Builder(
                ToolPermissionContext permissionContext,
                ToolInvocation invocation) {

            this.permissionContext = Objects.requireNonNull(permissionContext, "permissionContext cannot be null");
            this.invocation = Objects.requireNonNull(invocation, "invocation cannot be null");

            this.tenantId = permissionContext.tenantId();
            this.userId = permissionContext.userId();
            this.roles = permissionContext.roles() != null ? new HashSet<>(permissionContext.roles()) : Set.of();
            this.executionId = permissionContext.executionId();
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder agentId(String agentId) {
            this.agentId = agentId;
            return this;
        }

        public Builder roles(Set<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder executionId(String executionId) {
            this.executionId = executionId;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder parentExecutionId(String parentExecutionId) {
            this.parentExecutionId = parentExecutionId;
            return this;
        }

        public Builder deadline(Instant deadline) {
            this.deadline = deadline;
            return this;
        }

        public Builder resources(Map<String, String> resources) {
            this.resources = resources;
            return this;
        }

        public Builder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public PolicyEvaluationContext build() {
            return new PolicyEvaluationContext(
                    permissionContext,
                    invocation,
                    tenantId,
                    userId,
                    agentId,
                    roles,
                    executionId,
                    correlationId,
                    parentExecutionId,
                    deadline,
                    resources,
                    attributes
            );
        }
    }
}
