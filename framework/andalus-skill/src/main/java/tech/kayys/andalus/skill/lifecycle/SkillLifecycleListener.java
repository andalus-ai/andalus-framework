package tech.kayys.andalus.skill.lifecycle;

/**
 * Listener notified of Skill lifecycle transitions.
 *
 * @see SkillLifecycleManager
 */
@FunctionalInterface
public interface SkillLifecycleListener {

    /**
     * Called when a Skill lifecycle transition occurs.
     *
     * <p>Implementations must not throw exceptions; any errors should be
     * handled internally and must not prevent other listeners from being
     * notified.</p>
     *
     * @param event the lifecycle event; never {@code null}
     */
    void onLifecycleEvent(SkillLifecycleEvent event);
}
