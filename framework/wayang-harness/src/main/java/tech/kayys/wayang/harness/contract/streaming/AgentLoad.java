package tech.kayys.wayang.harness.contract.streaming;

/**
 * Metrics representing an agent's load for scheduling admission and backpressure.
 */
public record AgentLoad(
        int activeTasks,
        int queueDepth,
        int maxCapacity
) {
    public boolean hasCapacity() {
        return (activeTasks + queueDepth) < maxCapacity;
    }

    public static AgentLoad of(int activeTasks, int queueDepth, int maxCapacity) {
        return new AgentLoad(activeTasks, queueDepth, maxCapacity);
    }
}
