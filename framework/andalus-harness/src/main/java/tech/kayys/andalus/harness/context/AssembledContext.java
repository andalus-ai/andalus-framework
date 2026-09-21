package tech.kayys.andalus.harness.context;

import java.util.List;
import java.util.Map;

/**
 * Defines the contract for assembled context operations in the Andalus framework.
 */


public interface AssembledContext {

    ContextId id();

    List<ContextItem> items();

    Map<String, Object> metadata();

    long estimatedTokens();
}
