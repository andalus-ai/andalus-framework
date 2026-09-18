package tech.kayys.wayang.harness.journal;

/**
 * Standard event vocabulary representing state transitions in the durable journal.
 */
public enum ExecutionEventType {
    GRAPH_SUBMITTED,
    GRAPH_STARTED,
    NODE_READY,
    NODE_ADMITTED,
    NODE_STARTED,
    NODE_COMPLETED,
    NODE_FAILED,
    NODE_SKIPPED,
    CHECKPOINT_TAKEN,
    GRAPH_COMPLETED,
    GRAPH_FAILED,
    GRAPH_PAUSED,
    GRAPH_RESUMED,
    GRAPH_CANCELLED
}
