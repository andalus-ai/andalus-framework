package tech.kayys.andalus.execution.governance;

/**
 * A {@link ToolPolicy} that declares its authorization scope (tenant, agent, user, role, etc.).
 */
public interface AuthorizationPolicy extends ToolPolicy {

    AuthorizationScope scope();
}
