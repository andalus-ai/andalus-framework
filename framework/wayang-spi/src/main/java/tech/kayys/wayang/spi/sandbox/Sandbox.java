package tech.kayys.wayang.spi.sandbox;

/**
 * Primary abstraction representing an execution isolation instance.
 */
public interface Sandbox extends AutoCloseable {

    default SandboxDescriptor descriptor() {
        return null;
    }

    default SandboxState state() {
        return SandboxState.CREATED;
    }

    default SandboxContext context() {
        return null;
    }

    void start() throws Exception;

    void stop() throws Exception;

    default void destroy() throws Exception {
        stop();
    }

    @Override
    default void close() throws Exception {
        destroy();
    }

    /**
     * Backward-compatible helper to execute a command inside the sandbox.
     */
    default SandboxExecutionResult executeCommand(String command, long timeoutMillis) throws Exception {
        throw new UnsupportedOperationException("executeCommand not supported by this sandbox instance");
    }

    /**
     * Backward-compatible helper to write file content inside the sandbox.
     */
    default void writeFile(String path, String content) throws Exception {
        throw new UnsupportedOperationException("writeFile not supported by this sandbox instance");
    }

    /**
     * Backward-compatible helper to read file content inside the sandbox.
     */
    default String readFile(String path) throws Exception {
        throw new UnsupportedOperationException("readFile not supported by this sandbox instance");
    }
}
