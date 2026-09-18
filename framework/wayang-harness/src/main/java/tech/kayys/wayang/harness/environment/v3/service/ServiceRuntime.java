package tech.kayys.wayang.harness.environment.v3.service;

/**
 * Runtime SPI for acquiring access to external services under harness governance.
 */
public interface ServiceRuntime {

    ServiceHandle acquire(ServiceRequest request);
}
