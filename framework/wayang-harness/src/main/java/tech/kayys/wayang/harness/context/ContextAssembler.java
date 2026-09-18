package tech.kayys.wayang.harness.context;

/**
 * Defines the contract for context assembler operations in the Wayang framework.
 */


public interface ContextAssembler {

    AssembledContext assemble(ContextRequest request);
}
