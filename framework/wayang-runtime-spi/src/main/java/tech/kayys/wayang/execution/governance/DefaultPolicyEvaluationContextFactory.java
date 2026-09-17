package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolContext;
import tech.kayys.wayang.tool.ToolInvocation;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Default implementation of {@link PolicyEvaluationContextFactory} extracting metadata from {@link ToolContext}.
 */
public final class DefaultPolicyEvaluationContextFactory implements PolicyEvaluationContextFactory {

    @Override
    @SuppressWarnings("unchecked")
    public PolicyEvaluationContext create(ToolInvocation invocation, ToolContext toolContext) {
        Objects.requireNonNull(invocation, "invocation cannot be null");
        Objects.requireNonNull(toolContext, "toolContext cannot be null");

        Map<String, Object> attrs = toolContext.attributes();

        String tenantId = getString(attrs, "tenantId");
        String userId = getString(attrs, "userId");
        String agentId = getString(attrs, "agentId");
        String executionId = getString(attrs, "executionId");
        String correlationId = getString(attrs, "correlationId");
        String parentExecutionId = getString(attrs, "parentExecutionId");
        Instant deadline = attrs.get("deadline") instanceof Instant inst ? inst : null;

        Set<String> roles = new HashSet<>();
        Object rolesObj = attrs.get("roles");
        if (rolesObj instanceof Collection<?> c) {
            for (Object r : c) {
                if (r != null) {
                    roles.add(r.toString());
                }
            }
        }

        Map<String, String> resources = Map.of();
        Object resObj = attrs.get("resources");
        if (resObj instanceof Map<?, ?> m) {
            var map = new java.util.HashMap<String, String>();
            for (Map.Entry<?, ?> e : m.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    map.put(e.getKey().toString(), e.getValue().toString());
                }
            }
            resources = Map.copyOf(map);
        }

        ToolCapabilityLevel level = ToolCapabilityLevel.READ;
        if (attrs.get("level") instanceof ToolCapabilityLevel l) {
            level = l;
        }

        ToolPermissionContext permCtx = new ToolPermissionContext(
                tenantId,
                userId,
                executionId != null ? executionId : "exec-" + System.nanoTime(),
                List.copyOf(roles),
                invocation.name(),
                level
        );

        return new PolicyEvaluationContext(
                permCtx,
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
                attrs
        );
    }

    private static String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }
}
