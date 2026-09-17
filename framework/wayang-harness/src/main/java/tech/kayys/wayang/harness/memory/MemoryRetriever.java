package tech.kayys.wayang.harness.memory;

import java.util.Map;

public interface MemoryRetriever {

    MemoryQueryResult retrieve(MemoryQuery query, Map<String, Object> context);
}
