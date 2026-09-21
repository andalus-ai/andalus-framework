package tech.kayys.andalus.state.retention;

public interface GarbageCollector {
    int collect(RetentionPolicy policy);
}
