package tech.kayys.wayang.execution.governance;

import java.util.List;

/**
 * Registry for managing active {@link ToolPolicy} instances.
 */
public interface ToolPolicyRegistry {

    void register(ToolPolicy policy);

    void unregister(ToolPolicy policy);

    List<ToolPolicy> policies();
}
