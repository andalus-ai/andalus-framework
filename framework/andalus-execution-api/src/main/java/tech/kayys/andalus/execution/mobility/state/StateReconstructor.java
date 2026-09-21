package tech.kayys.andalus.execution.mobility.state;

/**
 * Provider SPI for reconstructing ephemeral runtime state and handles from durable envelope.
 */
public interface StateReconstructor {

    /**
     * Reconstruct execution runtime handles and state in the specified target runtime.
     */
    ReconstructionResult reconstruct(ExecutionContextEnvelope context, String targetRuntimeId);
}
