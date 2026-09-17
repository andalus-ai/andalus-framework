package tech.kayys.wayang.harness.api;

import java.util.concurrent.CompletableFuture;

/**
 * Canonical entry point to the Wayang Agent Harness.
 *
 * <p>Serves as the execution environment, governance boundary, and lifecycle coordinator
 * for all agents (coding, research, support, workflow) running within Wayang.</p>
 */
public interface Harness {

    /**
     * Starts an asynchronous agent execution within the Harness.
     *
     * @param request the invocation parameters and constraints
     * @return a handle to observe, manage, and await the execution
     */
    HarnessExecution start(HarnessRequest request);

    /**
     * Executes an agent task to completion asynchronously.
     *
     * @param request the invocation parameters
     * @return a future completing with the execution result
     */
    default CompletableFuture<HarnessResult> execute(HarnessRequest request) {
        return start(request).completion();
    }
}
