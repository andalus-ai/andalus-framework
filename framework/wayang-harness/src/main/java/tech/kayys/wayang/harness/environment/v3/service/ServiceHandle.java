package tech.kayys.wayang.harness.environment.v3.service;

import java.net.URI;
import java.util.Map;

/**
 * Handle representing an active external service connection.
 */
public interface ServiceHandle extends AutoCloseable {

    String serviceType();

    String serviceName();

    URI endpoint();

    Map<String, String> connectionDetails();

    @Override
    void close();
}
