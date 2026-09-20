package tech.kayys.wayang.execution.replay;

/**
 * Isolated deterministic virtual environment executing a replay or simulation.
 */
public interface ReplayEnvironment {

    String id();

    ClockProvider clock();

    RandomProvider random();

    EffectSandbox effects();
}
