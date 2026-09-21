package tech.kayys.andalus.execution.governance;

/**
 * Extension of {@link ToolPolicy} with explicit enabled/disabled state.
 */
public interface IdentifiedToolPolicy extends ToolPolicy {

    default boolean enabled() {
        return true;
    }
}
