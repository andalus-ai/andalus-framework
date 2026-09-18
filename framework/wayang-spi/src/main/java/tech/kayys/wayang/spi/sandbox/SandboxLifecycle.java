package tech.kayys.wayang.spi.sandbox;

public interface SandboxLifecycle {

    void start() throws Exception;

    void stop() throws Exception;

    void destroy() throws Exception;
}
