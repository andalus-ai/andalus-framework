package tech.kayys.wayang.tool;

/**
 * Logger contract for tool execution logs.
 */
public interface ToolLogger {

    void log(String level, String message);

    default void info(String message) {
        log("INFO", message);
    }

    default void warn(String message) {
        log("WARN", message);
    }

    default void error(String message) {
        log("ERROR", message);
    }

    static ToolLogger noop() {
        return (level, message) -> {};
    }
}
