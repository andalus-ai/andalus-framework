package tech.kayys.wayang.harness.fabric;

/**
 * Contract representing an execution worker in the distributed fabric.
 */
public interface ExecutionWorker {

    WorkerId id();

    WorkerCapabilities capabilities();

    WorkerStatus status();

    WorkerResources resources();

    WorkerHealth health();

    WorkerLease lease();
}
