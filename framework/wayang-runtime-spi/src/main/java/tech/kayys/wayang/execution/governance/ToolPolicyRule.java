package tech.kayys.wayang.execution.governance;

import java.util.Map;
import java.util.Set;

/**
 * Declarative governance rule matching on tools, permissions, roles, tenants, users, and resources.
 */
public record ToolPolicyRule(
        String id,
        Set<String> permissions,
        Set<String> tools,
        Set<String> roles,
        Set<String> tenants,
        Set<String> users,
        Map<String, Set<String>> resources,
        PolicyEffect effect,
        int priority
) {

    public ToolPolicyRule {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Policy rule id cannot be null or blank");
        }

        id = id.trim();

        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
        tools = tools == null ? Set.of() : Set.copyOf(tools);
        roles = roles == null ? Set.of() : Set.copyOf(roles);
        tenants = tenants == null ? Set.of() : Set.copyOf(tenants);
        users = users == null ? Set.of() : Set.copyOf(users);
        resources = resources == null ? Map.of() : Map.copyOf(resources);

        if (effect == null) {
            throw new IllegalArgumentException("Policy effect cannot be null");
        }
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final String id;
        private Set<String> permissions = Set.of();
        private Set<String> tools = Set.of();
        private Set<String> roles = Set.of();
        private Set<String> tenants = Set.of();
        private Set<String> users = Set.of();
        private Map<String, Set<String>> resources = Map.of();
        private PolicyEffect effect = PolicyEffect.ALLOW;
        private int priority = 100;

        public Builder(String id) {
            this.id = id;
        }

        public Builder permissions(Set<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder tools(Set<String> tools) {
            this.tools = tools;
            return this;
        }

        public Builder roles(Set<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder tenants(Set<String> tenants) {
            this.tenants = tenants;
            return this;
        }

        public Builder users(Set<String> users) {
            this.users = users;
            return this;
        }

        public Builder resources(Map<String, Set<String>> resources) {
            this.resources = resources;
            return this;
        }

        public Builder effect(PolicyEffect effect) {
            this.effect = effect;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public ToolPolicyRule build() {
            return new ToolPolicyRule(id, permissions, tools, roles, tenants, users, resources, effect, priority);
        }
    }
}
