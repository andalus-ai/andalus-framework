package tech.kayys.andalus.skill.runtime;

public enum SkillExecutionState {

    CREATED,
    RESOLVING,
    RUNNING,
    WAITING,
    COMPLETED,
    FAILED,
    CANCELED,
    TIMED_OUT
}
