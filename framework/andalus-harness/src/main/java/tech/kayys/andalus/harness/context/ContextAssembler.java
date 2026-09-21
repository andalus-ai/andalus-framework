package tech.kayys.andalus.harness.context;

/**
 * Defines the contract for context assembler operations in the Andalus framework.
 */


public interface ContextAssembler {

    AssembledContext assemble(ContextRequest request);
}
