package tech.kayys.wayang.harness.context;

import java.util.List;
import java.util.Map;

/**
 * Defines the contract for assembled context operations in the Wayang framework.
 */


public interface AssembledContext {

    ContextId id();

    List<ContextItem> items();

    Map<String, Object> metadata();

    long estimatedTokens();
}
