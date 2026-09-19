package tech.kayys.wayang.state.core;

import tech.kayys.wayang.state.retention.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultGarbageCollector implements GarbageCollector {

    private final List<StateOrArtifact> registry = new CopyOnWriteArrayList<>();

    public void register(StateOrArtifact item) {
        registry.add(item);
    }

    public void registerAll(Collection<StateOrArtifact> items) {
        registry.addAll(items);
    }

    @Override
    public int collect(RetentionPolicy policy) {
        Objects.requireNonNull(policy, "policy cannot be null");
        int collected = 0;
        var it = registry.iterator();
        while (it.hasNext()) {
            StateOrArtifact item = it.next();
            RetentionDecision decision = policy.evaluate(item);
            if (decision.deletePayload()) {
                registry.remove(item);
                collected++;
            }
        }
        return collected;
    }

    public int size() {
        return registry.size();
    }
}
