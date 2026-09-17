package tech.kayys.wayang.harness.context;

import java.util.List;
import java.util.Map;

public interface AssembledContext {

    ContextId id();

    List<ContextItem> items();

    Map<String, Object> metadata();

    long estimatedTokens();
}
