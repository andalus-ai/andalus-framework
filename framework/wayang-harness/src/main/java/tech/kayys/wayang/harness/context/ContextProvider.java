package tech.kayys.wayang.harness.context;

/**
 * Defines the contract for context provider operations in the Wayang framework.
 */


public interface ContextProvider {

    ContextContribution provide(ContextRequest request);
}
