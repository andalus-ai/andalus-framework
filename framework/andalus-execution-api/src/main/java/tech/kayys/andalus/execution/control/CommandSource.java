package tech.kayys.andalus.execution.control;

/**
 * Origin source of a control command.
 */
public enum CommandSource {
    SYSTEM,
    USER,
    POLICY,
    SUPERVISOR,
    RECOVERY,
    SCHEDULER,
    WORKFLOW
}
