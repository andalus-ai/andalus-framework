package tech.kayys.andalus.harness.context;

/**
 * Defines the contract for context provider operations in the Andalus framework.
 */


public interface ContextProvider {

    ContextContribution provide(ContextRequest request);
}
