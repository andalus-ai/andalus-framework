package tech.kayys.andalus.harness.isolation;

/**
 * Process isolation boundaries.
 */
public enum ProcessIsolation {
    NONE,
    CHILD_PROCESS,
    SANDBOXED_PROCESS,
    CONTAINERIZED,
    VM_ISOLATED
}
