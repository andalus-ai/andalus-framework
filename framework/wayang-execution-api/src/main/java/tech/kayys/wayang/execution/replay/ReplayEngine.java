package tech.kayys.wayang.execution.replay;

/**
 * Executes replay or simulation based on manifest within a given ReplayEnvironment.
 */
public interface ReplayEngine {

    ReplayResult replay(ReplayManifest manifest, ReplayEnvironment environment);
}
