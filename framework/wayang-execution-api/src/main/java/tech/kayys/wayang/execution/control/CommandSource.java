package tech.kayys.wayang.execution.control;

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
