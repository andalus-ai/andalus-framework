package tech.kayys.wayang.harness.tool;

public interface ToolLogger {

    void info(String message);

    void warn(String message);

    void error(String message, Throwable throwable);

    static ToolLogger noop() {
        return new ToolLogger() {
            @Override public void info(String message) {}
            @Override public void warn(String message) {}
            @Override public void error(String message, Throwable throwable) {}
        };
    }
}
