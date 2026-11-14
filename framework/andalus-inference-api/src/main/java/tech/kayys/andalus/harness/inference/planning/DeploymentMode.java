package tech.kayys.andalus.harness.inference.planning;

/**
 * Deployment constraint governing whether inference candidates must run locally, in cloud, or hybrid.
 */
public enum DeploymentMode {
    LOCAL_ONLY,
    CLOUD_ONLY,
    HYBRID
}
