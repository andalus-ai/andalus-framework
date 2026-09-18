package tech.kayys.wayang.harness.memory;

import java.util.Map;

/**
 * Defines the contract for memory retriever operations in the Wayang framework.
 */


public interface MemoryRetriever {

    MemoryQueryResult retrieve(MemoryQuery query, Map<String, Object> context);
}
