package tech.kayys.andalus.harness.phase;

import tech.kayys.andalus.harness.context.HarnessContext;
import tech.kayys.andalus.harness.runtime.HarnessRuntime;

/**
 * Modular execution phase in the Harness processing pipeline.
 *
 * @param <I> phase input type
 * @param <O> phase output type
 */
public interface HarnessPhase<I, O> {

    O execute(I input, HarnessContext context, HarnessRuntime runtime);
}
