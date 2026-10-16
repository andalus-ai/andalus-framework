package tech.kayys.andalus.memory.reactive;

@FunctionalInterface
public interface MemoryEventListener {
    void onMemoryEvent(MemoryEvent event);
}
