package tech.kayys.wayang.spi.sandbox;

public interface ResourceController {

    ResourceLimitStatus status();

    void enforce(SandboxLimits limits) throws Exception;

    void release() throws Exception;
}
