package tech.kayys.wayang.harness.observability.metrics;

import java.time.Duration;

/**
 * Universal metrics SPI for capturing runtime measurements without external dependency lock-in.
 */
public interface MetricsRuntime {

    Counter counter(String name);

    Gauge gauge(String name);

    Timer timer(String name);

    interface Counter {
        void increment();
        void add(long delta);
        long count();
    }

    interface Gauge {
        void set(double value);
        double value();
    }

    interface Timer {
        void record(Duration duration);
        long count();
        Duration totalTime();
    }

    static MetricsRuntime inMemory() {
        return new InMemoryMetricsRuntime();
    }
}
