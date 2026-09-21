package tech.kayys.andalus.harness.spi;

/**
 * Defines the contract for harness phase operations in the Andalus framework.
 */

public interface HarnessPhase<I, O> {
    O execute(I input, HarnessContext context, HarnessRuntime runtime);
}
