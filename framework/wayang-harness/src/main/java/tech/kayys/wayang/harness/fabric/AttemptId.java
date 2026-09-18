package tech.kayys.wayang.harness.fabric;

/**
 * Execution attempt counter for retry tracking.
 */
public record AttemptId(int number) {

    public static AttemptId initial() {
        return new AttemptId(1);
    }

    public static AttemptId of(int number) {
        return new AttemptId(number);
    }

    public AttemptId next() {
        return new AttemptId(number + 1);
    }
}
