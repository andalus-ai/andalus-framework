package tech.kayys.wayang.execution.limits;

/**
 * Process and thread count limits inside a sandbox.
 */
public record ProcessLimit(
        int maxProcesses,
        int maxThreads
) {

    public static ProcessLimit of(int processes, int threads) {
        return new ProcessLimit(processes, threads);
    }

    public static ProcessLimit unlimited() {
        return new ProcessLimit(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
}
