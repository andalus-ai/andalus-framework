package tech.kayys.wayang.harness.observability.metrics;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Thread-safe default in-memory implementation of {@link MetricsRuntime}.
 */
public class InMemoryMetricsRuntime implements MetricsRuntime {

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final Map<String, Gauge> gauges = new ConcurrentHashMap<>();
    private final Map<String, Timer> timers = new ConcurrentHashMap<>();

    @Override
    public Counter counter(String name) {
        return counters.computeIfAbsent(name, k -> new InMemoryCounter());
    }

    @Override
    public Gauge gauge(String name) {
        return gauges.computeIfAbsent(name, k -> new InMemoryGauge());
    }

    @Override
    public Timer timer(String name) {
        return timers.computeIfAbsent(name, k -> new InMemoryTimer());
    }

    private static class InMemoryCounter implements Counter {
        private final AtomicLong value = new AtomicLong(0);

        @Override public void increment() { value.incrementAndGet(); }
        @Override public void add(long delta) { value.addAndGet(delta); }
        @Override public long count() { return value.get(); }
    }

    private static class InMemoryGauge implements Gauge {
        private volatile double val = 0.0;

        @Override public void set(double value) { this.val = value; }
        @Override public double value() { return val; }
    }

    private static class InMemoryTimer implements Timer {
        private final AtomicLong count = new AtomicLong(0);
        private final AtomicLong totalNanos = new AtomicLong(0);

        @Override
        public void record(Duration duration) {
            if (duration != null) {
                count.incrementAndGet();
                totalNanos.addAndGet(duration.toNanos());
            }
        }

        @Override public long count() { return count.get(); }
        @Override public Duration totalTime() { return Duration.ofNanos(totalNanos.get()); }
    }
}
