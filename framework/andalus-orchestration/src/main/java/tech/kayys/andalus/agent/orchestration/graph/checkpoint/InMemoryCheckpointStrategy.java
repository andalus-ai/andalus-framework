package tech.kayys.andalus.agent.orchestration.graph.checkpoint;

import tech.kayys.andalus.agent.orchestration.graph.state.GraphState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A lightweight, thread-safe, in-memory check-pointer. 
 * Ideal for local execution via Andalus-CLI.
 */
public class InMemoryCheckpointStrategy implements CheckpointStrategy {

    private final Map<String, GraphState> store = new ConcurrentHashMap<>();

    @Override
    public void save(String threadId, GraphState state) {
        store.put(threadId, state);
    }

    @Override
    public GraphState load(String threadId) {
        return store.get(threadId);
    }
}
