package tech.kayys.andalus.harness.memory;

import java.util.Map;

/**
 * Defines the contract for memory retriever operations in the Andalus framework.
 */


public interface MemoryRetriever {

    MemoryQueryResult retrieve(MemoryQuery query, Map<String, Object> context);
}
