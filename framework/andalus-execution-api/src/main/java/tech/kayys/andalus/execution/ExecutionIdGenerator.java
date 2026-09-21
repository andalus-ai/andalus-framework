package tech.kayys.andalus.execution;

/**
 * Generates unique identifiers for executions and traces.
 */
public interface ExecutionIdGenerator {

    /** Generates a new unique execution ID. */
    String generate();

    /** Generates a new trace ID for correlation across execution trees. */
    String generateTrace();
}
