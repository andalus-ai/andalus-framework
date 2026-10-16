package tech.kayys.andalus.observability;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultObservabilityPublisher implements ObservabilityPublisher {

    private final List<ObservabilitySink> sinks = new CopyOnWriteArrayList<>();
    private volatile boolean enabled = true;

    public DefaultObservabilityPublisher() {
    }

    public DefaultObservabilityPublisher(List<ObservabilitySink> sinks) {
        if (sinks != null) {
            sinks.stream().filter(Objects::nonNull).forEach(this.sinks::add);
        }
    }

    public void addSink(ObservabilitySink sink) {
        if (sink != null) {
            this.sinks.add(sink);
        }
    }

    public void removeSink(ObservabilitySink sink) {
        this.sinks.remove(sink);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public void publish(ObservationEvent event) {
        if (!enabled || event == null) {
            return;
        }
        for (ObservabilitySink sink : sinks) {
            try {
                if (sink.supports(event)) {
                    sink.publish(event);
                }
            } catch (Exception ignored) {
                // Observability sink failure should not propagate
            }
        }
    }
}
