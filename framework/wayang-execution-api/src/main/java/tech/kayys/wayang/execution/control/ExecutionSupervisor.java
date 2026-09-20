package tech.kayys.wayang.execution.control;

/**
 * Supervises executing processes, agents, and runtimes to determine lifecycle actions.
 */
public interface ExecutionSupervisor {

    SupervisionDecision supervise(SupervisionContext context);
}
