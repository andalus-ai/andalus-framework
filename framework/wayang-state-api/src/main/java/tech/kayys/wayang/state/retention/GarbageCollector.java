package tech.kayys.wayang.state.retention;

public interface GarbageCollector {
    int collect(RetentionPolicy policy);
}
