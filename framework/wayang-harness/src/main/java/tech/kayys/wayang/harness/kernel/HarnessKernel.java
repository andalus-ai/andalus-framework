package tech.kayys.wayang.harness.kernel;

/**
 * Wayang Harness Kernel: Composition root and lifecycle coordinator.
 * Minimal foundational kernel that owns lifecycle, composition, discovery, and runtime registration.
 */
public interface HarnessKernel {

    HarnessId id();

    HarnessState state();

    RuntimeRegistry runtimes();

    ServiceRegistry services();

    LifecycleCoordinator lifecycle();

    void initialize();

    void start();

    void stop();

    void destroy();
}
